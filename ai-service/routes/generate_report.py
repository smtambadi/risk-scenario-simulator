from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.redis_client import get_cache, set_cache
import json
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
generate_report_bp = Blueprint("generate_report", __name__)

@generate_report_bp.route("/generate-report", methods=["POST"])
def generate_report():
    data = request.get_json()
    if not data or not data.get("input"):
        return jsonify({"error": "input field is required"}), 400
    user_input = data["input"].strip()
    if len(user_input) < 10:
        return jsonify({"error": "input too short"}), 400

    # Check cache
    cache_key = f"report:{user_input}"
    cached = get_cache(cache_key)
    if cached:
        cached["meta"]["cached"] = True
        return jsonify(cached), 200

    with open(os.path.join(BASE_DIR, "prompts", "generate_report.txt"), "r") as f:
        prompt_template = f.read()
    prompt = prompt_template.replace("{input}", user_input)

    result = call_groq(prompt, temperature=0.3, max_tokens=1500)
    if result is None:
        return jsonify({
            "error": "AI service unavailable",
            "is_fallback": True,
            "title": "Service Unavailable",
            "executive_summary": "Report generation failed. Please try again.",
            "overview": "N/A",
            "top_items": [],
            "recommendations": [],
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