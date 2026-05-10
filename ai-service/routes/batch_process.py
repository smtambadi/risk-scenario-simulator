from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.redis_client import get_cache, set_cache
import json
import time

batch_process_bp = Blueprint("batch_process", __name__)

@batch_process_bp.route("/batch-process", methods=["POST"])
def batch_process():
    data = request.get_json()
    if not data or not data.get("items"):
        return jsonify({"error": "items array is required"}), 400

    items = data["items"]
    if not isinstance(items, list):
        return jsonify({"error": "items must be an array"}), 400
    if len(items) > 20:
        return jsonify({"error": "Maximum 20 items allowed per batch"}), 400
    if len(items) == 0:
        return jsonify({"error": "items array cannot be empty"}), 400

    results = []
    total_tokens = 0
    start_time = time.time()

    for i, item in enumerate(items):
        text = item if isinstance(item, str) else item.get("input", item.get("text", ""))
        if not text or len(text.strip()) < 10:
            results.append({
                "index": i,
                "status": "error",
                "error": "Input too short or missing"
            })
            continue

        # Check cache
        cache_key = f"batch:{text.strip()[:200]}"
        cached = get_cache(cache_key)
        if cached:
            cached["index"] = i
            cached["status"] = "success"
            cached["cached"] = True
            results.append(cached)
            continue

        prompt = f"""Analyze the following risk scenario briefly. Provide a 2-sentence summary and a risk level (LOW, MEDIUM, HIGH, CRITICAL).

Return ONLY valid JSON:
{{"summary": "", "risk_level": ""}}

Risk: {text.strip()}"""

        result = call_groq(prompt, temperature=0.3, max_tokens=300)
        if result is None:
            results.append({
                "index": i,
                "status": "fallback",
                "summary": "AI analysis unavailable. Manual review recommended.",
                "risk_level": "MEDIUM",
                "is_fallback": True
            })
        else:
            total_tokens += result["meta"].get("tokens_used", 0)
            try:
                parsed = json.loads(result["content"])
                parsed["index"] = i
                parsed["status"] = "success"
                results.append(parsed)
                set_cache(cache_key, parsed)
            except Exception:
                results.append({
                    "index": i,
                    "status": "success",
                    "raw_response": result["content"]
                })

        # 100ms delay between items to avoid rate limiting
        if i < len(items) - 1:
            time.sleep(0.1)

    elapsed_ms = round((time.time() - start_time) * 1000)

    return jsonify({
        "results": results,
        "total_processed": len(results),
        "successful": sum(1 for r in results if r.get("status") == "success"),
        "failed": sum(1 for r in results if r.get("status") == "error"),
        "fallback": sum(1 for r in results if r.get("status") == "fallback"),
        "meta": {
            "total_tokens_used": total_tokens,
            "response_time_ms": elapsed_ms,
            "model_used": "llama-3.3-70b-versatile"
        }
    }), 200
