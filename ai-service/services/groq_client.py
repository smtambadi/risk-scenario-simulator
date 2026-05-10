import os
import time
import logging
from groq import Groq
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)
client = Groq(api_key=os.getenv("GROQ_API_KEY"))

MODEL_NAME = "llama-3.3-70b-versatile"


def call_groq(prompt: str, temperature: float = 0.3, max_tokens: int = 1000):
    """
    Call Groq API with retry logic.
    Returns a dict: {"content": str, "meta": {...}} on success, or None on failure.
    """
    retries = 3
    for attempt in range(retries):
        try:
            start_time = time.time()
            response = client.chat.completions.create(
                model=MODEL_NAME,
                messages=[
                    {
                        "role": "user",
                        "content": prompt
                    }
                ],
                temperature=temperature,
                max_tokens=max_tokens
            )
            elapsed_ms = round((time.time() - start_time) * 1000)

            content = response.choices[0].message.content
            usage = response.usage

            meta = {
                "model_used": MODEL_NAME,
                "tokens_used": usage.total_tokens if usage else 0,
                "prompt_tokens": usage.prompt_tokens if usage else 0,
                "completion_tokens": usage.completion_tokens if usage else 0,
                "response_time_ms": elapsed_ms,
                "cached": False,
                "confidence": 0.85  # default confidence
            }

            return {"content": content, "meta": meta}

        except Exception as e:
            logger.error(f"Groq call failed (attempt {attempt + 1}): {e}")
            if attempt < retries - 1:
                time.sleep(2 ** attempt)
            else:
                return None