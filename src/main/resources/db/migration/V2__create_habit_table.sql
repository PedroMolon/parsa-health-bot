CREATE TABLE tb_habit (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_habit_user_account FOREIGN KEY (chat_id) REFERENCES tb_user_accounts(chat_id) ON DELETE CASCADE
);

CREATE TABLE tb_habit_log (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_habit_log_habit FOREIGN KEY (habit_id) REFERENCES tb_habit(id) ON DELETE CASCADE,
    CONSTRAINT fk_habit_log_user_account FOREIGN KEY (chat_id) REFERENCES tb_user_accounts(chat_id) ON DELETE CASCADE
);

CREATE INDEX idx_habit_user ON tb_habit(chat_id);
CREATE INDEX idx_habit_log_user_date ON tb_habit_log(chat_id, completed_at);