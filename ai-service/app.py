from flask import Flask, request, jsonify
from flask_cors import CORS
from services.security import validate_input, sanitize
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
import time
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

app.config["RATELIMIT_HEADERS_ENABLED"] = True

# Rate limiter setup
limiter = Limiter(
    key_func=get_remote_address,
    default_limits=["30 per minute"]
)
limiter.init_app(app)

# Track startup time for uptime calculation
app.config["START_TIME"] = time.time()

# Register ALL route blueprints
from routes.describe import describe_bp
from routes.recommend import recommend_bp
from routes.categorise import categorise_bp
from routes.query import query_bp
from routes.generate_report import generate_report_bp
from routes.generate_report_stream import generate_report_stream_bp
from routes.analyse_document import analyse_document_bp
from routes.health import health_bp
from routes.batch_process import batch_process_bp

app.register_blueprint(describe_bp)
app.register_blueprint(recommend_bp)
app.register_blueprint(categorise_bp)
app.register_blueprint(query_bp)
app.register_blueprint(generate_report_bp)
app.register_blueprint(generate_report_stream_bp)
app.register_blueprint(analyse_document_bp)
app.register_blueprint(health_bp)
app.register_blueprint(batch_process_bp)

# Seed ChromaDB with domain knowledge on startup
try:
    from seed_knowledge import seed_chromadb
    seed_chromadb()
except Exception as e:
    logger.warning(f"ChromaDB seeding skipped: {e}")

# 429 handler
@app.errorhandler(429)
def ratelimit_handler(e):
    return jsonify({
        "error": "Too many requests. Please try again later.",
        "retry_after": 60
    }), 429

# Global security middleware
@app.before_request
def security_layer():
    # Skip validation for GET, OPTIONS, HEAD requests
    if request.method in ("GET", "OPTIONS", "HEAD"):
        return

    # Skip for health endpoint
    if request.path == "/health":
        return

    data = request.get_json(silent=True)

    if data is None:
        return jsonify({"error": "Invalid or missing JSON body"}), 400

    found = False

    for key in ["input", "text", "query", "content", "question"]:
        if key in data:
            value = data[key]

            valid, error = validate_input(value)
            if not valid:
                return jsonify({"error": error}), 400

            request.sanitized_input = sanitize(value)
            request.sanitized_key = key
            found = True
            break

    # Allow requests with 'items' key (batch processing)
    if not found and "items" in data:
        found = True

    if not found:
        return jsonify({"error": "Missing valid input field"}), 400

# Root health check
@app.route("/")
def home():
    return jsonify({"status": "ok", "message": "Risk Scenario Simulator AI Service is running"})

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)