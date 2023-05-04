package com.directors.application.feedback;

import com.directors.domain.feedback.Feedback;
import com.directors.domain.feedback.FeedbackRating;
import com.directors.domain.feedback.FeedbackRepository;
import com.directors.domain.feedback.exception.FeedbackNotFoundException;
import com.directors.domain.question.Question;
import com.directors.domain.question.QuestionRepository;
import com.directors.domain.user.UserRepository;
import com.directors.infrastructure.exception.question.QuestionNotFoundException;
import com.directors.presentation.feedback.request.CreateFeedbackRequest;
import com.directors.presentation.feedback.request.UpdateFeedbackRequest;
import com.directors.presentation.feedback.response.GetByFeedbackIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void create(CreateFeedbackRequest request, String questionerId) {
        var question = getQuestionById(request.questionId());

        question.canCreateFeedback(questionerId);

        var feedback = Feedback.builder()
                .feedbackRating(FeedbackRating.fromValue(request.rating()))
                .description(request.description())
                .questioner(question.getQuestioner())
                .director(question.getDirector())
                .feedbackCheckList(request.toFeedbackCheckList())
                .build();

        feedbackRepository.save(feedback);
    }

    @Transactional
    public void update(UpdateFeedbackRequest request) {
        var feedback = getFeedbackById(request.feedbackId());
        feedback.updateFeedback(FeedbackRating.fromValue(request.rating()), request.toFeedbackCheckList(), request.description());
    }

    @Transactional
    public GetByFeedbackIdResponse getFeedbackByFeedbackId(Long feedbackId) {
        return GetByFeedbackIdResponse.of(getFeedbackById(feedbackId));
    }

    private Question getQuestionById(Long questionId) {
        return questionRepository
                .findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }

    private Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository
                .findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));
    }
}