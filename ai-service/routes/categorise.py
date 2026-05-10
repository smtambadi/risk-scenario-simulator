from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.redis_client import get_cache, set_cache
import json

categorise_bp = Blueprint("categorise", __name__)

CATEGORIES = ["Infrastructure", "Cybersecurity", "Financial", "Operational", "Compliance", "Strategic"]

@categorise_bp.route("/categorise", methods=["POST"])
def categorise():
    data = request.get_json()
    user_input = data.get("input") or data.get("text", "")
    if not user_input:
        return jsonify({"error": "input or text field is required"}), 400
    user_input = user_input.strip()
    if len(user_input) < 10:
        return jsonify({"error": "input too short"}), 400

    # Check cache
    cache_key = f"categorise:{user_input}"
    cached = get_cache(cache_key)
    if cached:
        cached["meta"]["cached"] = True
        return jsonify(cached), 200

    prompt = f"""You are an AI risk classifier for enterprise risk management.

Classify the following risk scenario into exactly ONE of these categories:
- Infrastructure
- Cybersecurity
- Financial
- Operational
- Compliance
- Strategic

Also provide:
- confidence (0.0 to 1.0)
- short reasoning (1-2 sentences)

Return ONLY valid JSON in this exact format:
{{
  "category": "",
  "confidence": 0.0,
  "reasoning": ""
}}

Risk scenario: {user_input}"""

    result = call_groq(prompt, temperature=0.3)

    if result is None:
        # Fallback: simple keyword-based classification
        lower_input = user_input.lower()
        if any(w in lower_input for w in ["server", "network", "database", "cloud", "infrastructure", "dns", "cluster"]):
            category = "Infrastructure"
        elif any(w in lower_input for w in ["hack", "breach", "vulnerability", "security", "attack", "ransomware", "phishing"]):
            category = "Cybersecurity"
        elif any(w in lower_input for w in ["revenue", "cost", "financial", "budget", "currency", "rate", "payment"]):
            category = "Financial"
        elif any(w in lower_input for w in ["supply", "production", "warehouse", "logistics", "staffing", "burnout"]):
            category = "Operational"
        elif any(w in lower_input for w in ["compliance", "regulation", "audit", "gdpr", "pci", "sox", "reporting"]):
            category = "Compliance"
        else:
            category = "Strategic"

        return jsonify({
            "category": category,
            "confidence": 0.6,
            "reasoning": "Classified using keyword-based fallback (AI unavailable)",
            "is_fallback": True,
            "meta": {
                "model_used": "fallback",
                "tokens_used": 0,
                "response_time_ms": 0,
                "cached": False,
                "confidence": 0.6
            }
        }), 200

    meta = result["meta"]
    try:
        parsed = json.loads(result["content"])
        # Override meta confidence with AI's confidence
        if "confidence" in parsed:
            meta["confidence"] = parsed["confidence"]
        response = {**parsed, "meta": meta}
    except Exception:
        response = {
            "category": "Unknown",
            "confidence": 0.0,
            "reasoning": "Failed to parse AI response",
            "raw_response": result["content"],
            "meta": meta
        }

    set_cache(cache_key, response)
    return jsonify(response), 200