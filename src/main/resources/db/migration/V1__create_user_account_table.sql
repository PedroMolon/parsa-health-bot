CREATE TABLE tb_user_accounts (
    chat_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    weight NUMERIC (5,2),
    height INT,
    age INT,
    activity_level VARCHAR(50),
    daily_water_goal_ml INT,
    time_zone VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);