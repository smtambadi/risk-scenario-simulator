from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.redis_client import get_cache, set_cache
from datetime import datetime
import json
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
describe_bp = Blueprint("describe", __name__)

@describe_bp.route("/describe", methods=["POST"])
def describe():
    data = request.get_json()
    if not data or not data.get("input"):
        return jsonify({"error": "input field is required"}), 400
    user_input = data["input"].strip()
    if len(user_input) < 10:
        return jsonify({"error": "input too short"}), 400

    # Check cache
    cache_key = f"describe:{user_input}"
    cached = get_cache(cache_key)
    if cached:
        cached["meta"]["cached"] = True
        return jsonify(cached), 200

    with open(os.path.join(BASE_DIR, "prompts", "describe.txt"), "r") as f:
        prompt_template = f.read()
    prompt = prompt_template.replace("{input}", user_input)
    prompt = prompt.replace("{generated_at}", datetime.utcnow().isoformat())

    result = call_groq(prompt, temperature=0.3)
    if result is None:
        return jsonify({"error": "AI service unavailable", "is_fallback": True}), 503

    meta = result["meta"]
    try:
        parsed = json.loads(result["content"])
        response = {**parsed, "meta": meta}
    except Exception:
        response = {"raw_response": result["content"], "meta": meta}

    # Cache the result
    set_cache(cache_key, response)
    return jsonify(response), 200