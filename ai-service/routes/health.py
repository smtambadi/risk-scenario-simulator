from flask import Blueprint, jsonify, current_app
from services.chroma_client import collection
from services.redis_client import get_stats as get_cache_stats
import time

health_bp = Blueprint("health", __name__)

@health_bp.route("/health", methods=["GET"])
def health():
    start_time = current_app.config.get("START_TIME", time.time())
    uptime_seconds = int(time.time() - start_time)

    # Get ChromaDB doc count
    try:
        doc_count = collection.count()
    except Exception:
        doc_count = 0

    # Get cache stats
    cache_stats = get_cache_stats()

    return jsonify({
        "status": "ok",
        "message": "AI Service is running",
        "model": "llama-3.3-70b-versatile",
        "provider": "Groq",
        "chromadb_documents": doc_count,
        "cache_stats": cache_stats,
        "uptime_seconds": uptime_seconds,
        "uptime_formatted": f"{uptime_seconds // 3600}h {(uptime_seconds % 3600) // 60}m {uptime_seconds % 60}s",
        "endpoints": [
            "POST /describe",
            "POST /recommend",
            "POST /categorise",
            "POST /query",
            "POST /generate-report",
            "POST /generate-report/stream",
            "POST /analyse-document",
            "POST /batch-process",
            "GET /health"
        ],
        "timestamp": int(time.time())
    })