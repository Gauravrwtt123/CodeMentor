-- Purani table mein sender_type column add karna
-- (Nayi table ke liye database/schema.sql dekho — usme ye pehle se hai)
ALTER TABLE chat_logs
ADD COLUMN sender_type VARCHAR(20) DEFAULT 'AI'; -- Values: 'AI', 'HUMAN', 'STUDENT'

