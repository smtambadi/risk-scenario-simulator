from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.redis_client import get_cache, set_cache
import json
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
analyse_document_bp = Blueprint("analyse_document", __name__)

@analyse_document_bp.route("/analyse-document", methods=["POST"])
def analyse_document():
    data = request.get_json()
    if not data or not data.get("input"):
        return jsonify({"error": "input field is required"}), 400
    user_input = data["input"].strip()
    if len(user_input) < 20:
        return jsonify({"error": "input too short (min 20 chars)"}), 400

    # Check cache
    cache_key = f"analyse:{user_input[:200]}"  # Truncate for cache key
    cached = get_cache(cache_key)
    if cached:
        cached["meta"]["cached"] = True
        return jsonify(cached), 200

    with open(os.path.join(BASE_DIR, "prompts", "analyse_document.txt"), "r") as f:
        prompt_template = f.read()
    prompt = prompt_template.replace("{input}", user_input)

    result = call_groq(prompt, temperature=0.3, max_tokens=1500)
    if result is None:
        return jsonify({
            "error": "AI service unavailable",
            "is_fallback": True,
            "findings": [],
            "summary": "Analysis failed. Please try again.",
            "total_findings": 0,
            "risk_count": 0,
            "insight_count": 0,
            "meta": {
                "model_used": "fallback",
                "tokens_used": 0,
                "response_time_ms": 0,
                "cached": False,
                "confidence": 0.0
            }
        }), 503

    meta = result["meta"]
    try:
        parsed = json.loads(result["content"])
        response = {**parsed, "meta": meta}
    except Exception:
        response = {"raw_response": result["content"], "meta": meta}

    set_cache(cache_key, response)
    return jsonify(response), 200