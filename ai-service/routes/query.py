from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.chroma_client import query_knowledge
from services.redis_client import get_cache, set_cache
import json
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
query_bp = Blueprint("query", __name__)

@query_bp.route("/query", methods=["POST"])
def query():
    data = request.get_json()
    if not data or not data.get("question"):
        return jsonify({"error": "question field is required"}), 400
    question = data["question"].strip()
    if len(question) < 5:
        return jsonify({"error": "question too short"}), 400

    # Check cache
    cache_key = f"query:{question}"
    cached = get_cache(cache_key)
    if cached:
        cached["meta"]["cached"] = True
        return jsonify(cached), 200

    chunks = query_knowledge(question, top_k=3)
    context = "\n\n".join(chunks) if chunks else "No relevant context found in knowledge base."

    with open(os.path.join(BASE_DIR, "prompts", "query.txt"), "r") as f:
        prompt_template = f.read()
    prompt = prompt_template.replace("{context}", context).replace("{question}", question)

    result = call_groq(prompt, temperature=0.3)
    if result is None:
        return jsonify({"error": "AI service unavailable", "is_fallback": True}), 503

    meta = result["meta"]
    response = {
        "answer": result["content"],
        "sources": chunks,
        "total_sources": len(chunks),
        "meta": meta
    }

    set_cache(cache_key, response)
    return jsonify(response), 200