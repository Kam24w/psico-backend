package com.psico.app.ai.repository;

import com.psico.app.ai.model.ResponseFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseFeedbackRepository extends JpaRepository<ResponseFeedback, Long> {
}
