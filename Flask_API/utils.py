import pandas as pd

# Maps the model_type key used in this API to the encoder group key in encoders.joblib
_ENCODER_GROUP = {
    'city':       'profile',
    'experience': 'profile',
    'trips':      'trip',
    'feed':       'feed',
}

def prepare_input_data(json_data, model_type, feature_columns, encoders):
    df = pd.DataFrame([json_data])

    required_cols = feature_columns[model_type]

    for col in required_cols:
        if col not in df.columns:
            df[col] = 0

    df = df[required_cols]

    col_encoders = encoders.get(_ENCODER_GROUP.get(model_type, ''), {})

    for col in df.columns:
        if col in col_encoders:
            enc = col_encoders[col]

            def safe_encode(val, enc=enc):
                if val in enc.classes_:
                    return enc.transform([val])[0]
                return 0

            df[col] = df[col].apply(safe_encode)

    # Zero out any column still holding a non-numeric value
    for col in df.select_dtypes(include='object').columns:
        df[col] = 0

    return df
