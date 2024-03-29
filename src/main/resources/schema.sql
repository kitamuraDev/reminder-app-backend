CREATE TABLE reminder (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256) NOT NULL,
  description TEXT NOT NULL,
  due_date DATE NOT NULL,
  priority INT CHECK (priority >= 0 AND priority <= 2),
  is_completed BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
