from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import pandas as pd
import os
from utils import prepare_input_data

app = Flask(__name__)
CORS(app)

# ==========================================
# GLOBAL LOADING BLOCK
# ==========================================
AI_BRAIN_PATH = os.path.join(os.path.dirname(__file__), "models")

print("Loading AI models...")
try:
    models = {
        'city':       joblib.load(os.path.join(AI_BRAIN_PATH, "model_city.joblib")),
        'experience': joblib.load(os.path.join(AI_BRAIN_PATH, "model_experience.joblib")),
        'trips':      joblib.load(os.path.join(AI_BRAIN_PATH, "model_trips.joblib")),
        'feed':       joblib.load(os.path.join(AI_BRAIN_PATH, "model_feed.joblib"))
    }
    encoders        = joblib.load(os.path.join(AI_BRAIN_PATH, "encoders.joblib"))
    feature_columns = joblib.load(os.path.join(AI_BRAIN_PATH, "feature_columns.joblib"))
    trips_catalog   = pd.read_csv(os.path.join(AI_BRAIN_PATH, "trips_catalog.csv"))
    print("All models loaded successfully.")
except Exception as e:
    raise RuntimeError(
        f"Failed to load models from '{AI_BRAIN_PATH}'. "
        f"Run 'python download_models.py' first. Details: {e}"
    ) from e


# ==========================================
# API ENDPOINTS
# ==========================================

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({"status": "AI Brain is online and ready!"})


@app.route('/api/recommend-city', methods=['POST'])
def recommend_city():
    data = request.json
    try:
        df_input = prepare_input_data(data, 'city', feature_columns, encoders)
        prediction_num = models['city'].predict(df_input)[0]
        target_encoder = encoders['profile'].get('recommended_city')
        if target_encoder:
            prediction_word = target_encoder.inverse_transform([prediction_num])[0]
        else:
            prediction_word = str(prediction_num)
        return jsonify({"recommended_city": prediction_word, "status": "success"})
    except Exception as e:
        return jsonify({"error": str(e), "status": "failed"}), 500


@app.route('/api/recommend-experience', methods=['POST'])
def recommend_experience():
    data = request.json
    try:
        df_input = prepare_input_data(data, 'experience', feature_columns, encoders)
        prediction_num = models['experience'].predict(df_input)[0]
        target_encoder = encoders['profile'].get('recommended_experience_type')
        if target_encoder:
            prediction_word = target_encoder.inverse_transform([prediction_num])[0]
        else:
            prediction_word = str(prediction_num)
        return jsonify({"recommended_experience": prediction_word, "status": "success"})
    except Exception as e:
        return jsonify({"error": str(e), "status": "failed"}), 500


@app.route('/api/recommend-trips', methods=['POST'])
def recommend_trips():
    user_data = request.json
    try:
        candidate_trips = user_data.get('candidate_trips', [])
        if not candidate_trips:
            fallback = trips_catalog.head(5).to_dict(orient='records')
            for t in fallback:
                t['trip_title'] = t.get('trip_name')
                t['trip_description'] = t.get('category')
                t['estimated_budget'] = t.get('budget_per_person_usd')
            return jsonify({"recommended_trips": fallback, "status": "success"})
        scored_trips = []
        for trip in candidate_trips:
            df_input = prepare_input_data(trip, 'trips', feature_columns, encoders)
            score = models['trips'].predict(df_input)[0]
            trip['ai_match_score'] = float(score)
            trip['trip_title'] = trip.get('trip_name')
            trip['trip_description'] = trip.get('category')
            trip['estimated_budget'] = trip.get('budget_per_person_usd')
            scored_trips.append(trip)
        scored_trips.sort(key=lambda x: x['ai_match_score'], reverse=True)
        # Renvoie tous les candidats scores : le backend Spring affine le classement
        # avec le profil du questionnaire puis ne garde que le top 5.
        return jsonify({"recommended_trips": scored_trips, "status": "success"})
    except Exception as e:
        return jsonify({"error": str(e), "status": "failed"}), 500


@app.route('/api/rank-feed', methods=['POST'])
def rank_feed():
    payload = request.json
    posts = payload.get('posts', [])
    if not posts:
        return jsonify({"error": "No posts provided", "status": "failed"}), 400
    try:
        scored_posts = []
        for post in posts:
            df_input = prepare_input_data(post, 'feed', feature_columns, encoders)
            score = models['feed'].predict(df_input)[0]
            post['ai_ranking_score'] = float(score)
            scored_posts.append(post)
        scored_posts.sort(key=lambda x: x['ai_ranking_score'], reverse=True)
        return jsonify({"ranked_feed": scored_posts, "status": "success"})
    except Exception as e:
        return jsonify({"error": str(e), "status": "failed"}), 500


@app.route('/', methods=['GET'])
def index():
    return jsonify({
        "name": "SmartTrip AI API",
        "status": "online",
        "endpoints": {
            "health":               "GET  /api/health",
            "recommend_city":       "POST /api/recommend-city",
            "recommend_experience": "POST /api/recommend-experience",
            "recommend_trips":      "POST /api/recommend-trips",
            "rank_feed":            "POST /api/rank-feed"
        },
        "docs": "https://github.com/PFE-Group7/smarttrip-backend/blob/master/AI_API_INTEGRATION_GUIDE.md"
    })


if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=False)
