"""
In-memory cache with TTL (15 minutes) using cachetools.
Uses SHA256 key hashing and tracks hit/miss counters.
Drop-in replacement for Redis — no external dependency needed.
"""
import hashlib
import time
import threading

# Cache configuration
CACHE_TTL = 900  # 15 minutes in seconds
MAX_CACHE_SIZE = 500

# Thread-safe cache store
_lock = threading.Lock()
_cache_store = {}  # key -> {"value": ..., "expires_at": float}
_stats = {"hits": 0, "misses": 0}


def _make_key(raw_key: str) -> str:
    """Generate SHA256 hash key."""
    return hashlib.sha256(raw_key.encode("utf-8")).hexdigest()


def _evict_expired():
    """Remove expired entries."""
    now = time.time()
    expired = [k for k, v in _cache_store.items() if v["expires_at"] < now]
    for k in expired:
        del _cache_store[k]


def get_cache(raw_key: str):
    """Get value from cache. Returns None if miss or expired."""
    key = _make_key(raw_key)
    with _lock:
        _evict_expired()
        entry = _cache_store.get(key)
        if entry and entry["expires_at"] > time.time():
            _stats["hits"] += 1
            return entry["value"]
        _stats["misses"] += 1
        return None


def set_cache(raw_key: str, value, ttl: int = CACHE_TTL):
    """Set value in cache with TTL."""
    key = _make_key(raw_key)
    with _lock:
        # Evict if at capacity
        if len(_cache_store) >= MAX_CACHE_SIZE:
            _evict_expired()
            # If still full, remove oldest
            if len(_cache_store) >= MAX_CACHE_SIZE:
                oldest_key = min(_cache_store, key=lambda k: _cache_store[k]["expires_at"])
                del _cache_store[oldest_key]

        _cache_store[key] = {
            "value": value,
            "expires_at": time.time() + ttl
        }


def get_stats() -> dict:
    """Return cache statistics."""
    with _lock:
        total = _stats["hits"] + _stats["misses"]
        return {
            "hits": _stats["hits"],
            "misses": _stats["misses"],
            "total_requests": total,
            "hit_rate": round(_stats["hits"] / total * 100, 1) if total > 0 else 0,
            "cached_entries": len(_cache_store)
        }


def clear_cache():
    """Clear all cache entries and reset stats."""
    with _lock:
        _cache_store.clear()
        _stats["hits"] = 0
        _stats["misses"] = 0