FROM python:3.11-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

# Tạo user không phải root
RUN useradd -m -u 10001 appuser
WORKDIR /app

# Cài deps
COPY music-recommendation/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source
COPY music-recommendation/ .

# (khuyến nghị) thêm endpoint health
# Nếu bạn bổ sung như bên dưới, giữ EXPOSE 8000
EXPOSE 8000
USER appuser

# Chạy uvicorn
CMD ["python", "-m", "uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
