package EduConnect.Service;

import EduConnect.Domain.Course;
import EduConnect.Domain.Exercise;
import EduConnect.Domain.Lesson;
import EduConnect.Domain.Request.AnswerRequest;
import EduConnect.Domain.Response.ScoreRes;
import EduConnect.Domain.TestExercise;
import EduConnect.Repository.ExerciseRepository;
import EduConnect.Repository.LessonRepository;
import EduConnect.Repository.TestExerciseRepository;
import EduConnect.Util.Enum.AnswerCorrect;
import EduConnect.Util.Enum.Dificulty;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final TestExerciseRepository testExerciseRepository;
    public ExerciseService(ExerciseRepository exerciseRepository, TestExerciseRepository testExerciseRepository) {
        this.exerciseRepository = exerciseRepository;
        this.testExerciseRepository = testExerciseRepository;
    }
    public Page<Exercise> findAll(Pageable pageable) {
        return exerciseRepository.findAll(pageable);
    }
    public Exercise findById(long id) {
        return this.exerciseRepository.findById(id).orElse(null);
    }

    public List<Exercise> excelExercise(MultipartFile file, long idTestExercise) {
        List<Exercise> exerciseList = new ArrayList<>();
        try {
            System.out.println("File type: " + file.getContentType());
            if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                throw new IllegalArgumentException("File không phải định dạng Excel (.xlsx)");
            }

            InputStream is = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Cell questionCell = row.getCell(0);
                if (questionCell == null || questionCell.getCellType() == CellType.BLANK ||
                        (questionCell.getCellType() == CellType.STRING && questionCell.getStringCellValue().trim().isEmpty())) {
                    continue;
                }

                Exercise exercise = new Exercise();
                exercise.setCauHoi(questionCell.getStringCellValue());

                exercise.setDapAn1(getCellStringValue(row.getCell(1)));
                exercise.setDapAn2(getCellStringValue(row.getCell(2)));
                exercise.setDapAn3(getCellStringValue(row.getCell(3)));
                exercise.setDapAn4(getCellStringValue(row.getCell(4)));

                String correctAnswer = getCellStringValue(row.getCell(5));
                if (correctAnswer != null && !correctAnswer.isEmpty()) {
                    try {
                        exercise.setDapAnDung(AnswerCorrect.valueOf(correctAnswer.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid Correct Answer value in row " + (row.getRowNum() + 1) + ": " + correctAnswer);
                    }
                }
                String difficultyAnswer = getCellStringValue(row.getCell(6));
                if (difficultyAnswer != null && !difficultyAnswer.isEmpty()) {
                    exercise.setDificulty(Dificulty.valueOf(difficultyAnswer.toUpperCase()));
                } else {
                    exercise.setDificulty(Dificulty.EASY);
                }

                Optional<TestExercise> lesson1 = testExerciseRepository.findById(idTestExercise);
                lesson1.ifPresent(exercise::setTestExercise);

                //String raw = getCellStringValue(row.getCell(7));
                Long idBaiHoc;
                idBaiHoc = lesson1.get().getLesson().getId();
//                if (raw != null && !raw.trim().isEmpty()) {
//                    try {
//                        idBaiHoc = (long) Double.parseDouble(raw.trim());
//                    } catch (NumberFormatException e) {
//                        throw new IllegalArgumentException("Invalid lesson ID at row " + (row.getRowNum() + 1));
//                    }
//                } else {
//                    if (lesson1.isPresent() && lesson1.get().getLesson() != null) {
//                        idBaiHoc = lesson1.get().getLesson().getId();
//                    } else {
//                        throw new IllegalArgumentException("Không tìm thấy bài học liên kết với TestExercise ID: " + idTestExercise);
//                    }
//                }

                exercise.setId_BaiHoc(idBaiHoc);

                exerciseList.add(exercise);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing Excel file: " + e.getMessage());
        }
        return exerciseRepository.saveAll(exerciseList);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        return String.valueOf(cell);
    }


    public long countExerciseByCourse(long idCourse) {
        return this.exerciseRepository.countExerciseByCourseId(idCourse);
    }

    public TestExercise findByTestExerciseId(long exerciseId){
        return this.exerciseRepository.findExerciseById(exerciseId);
    }

    @Transactional
    public Exercise save(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Optional<Exercise> findById(Long id) {
        return exerciseRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return exerciseRepository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        exerciseRepository.deleteById(id);
    }

    public List<Exercise> findByTestExerciseId(Long testExerciseId) {
        return exerciseRepository.findByTestExerciseId(testExerciseId);
    }

    public Page<Exercise> findByDificulty(Dificulty dificulty, Pageable pageable) {
        return exerciseRepository.findByDificulty(dificulty,pageable);
    }
    public ScoreRes scoreExercises(Long testExerciseId, List<AnswerRequest> answerRequests) {
        List<Exercise> exercises = exerciseRepository.findByTestExerciseId(testExerciseId);
        if (exercises.isEmpty()) {
            ScoreRes scoreRes = new ScoreRes();
            scoreRes.setScore(0.0f);
            scoreRes.setMessage("No exercises found for this lesson.");
            scoreRes.setResults(new ArrayList<>());
            return scoreRes;
        }

        if (answerRequests == null || answerRequests.isEmpty()) {
            ScoreRes scoreRes = new ScoreRes();
            scoreRes.setScore(0.0f);
            scoreRes.setMessage("No answers provided.");
            scoreRes.setResults(new ArrayList<>());
            return scoreRes;
        }

        Map<Long, Exercise> exerciseMap = exercises.stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));

        int answerCorrect = 0;
        float soNhan = exercises.size() / 10.0f;
        List<ScoreRes.ExerciseResult> results = new ArrayList<>();

        for (AnswerRequest answerRequest : answerRequests) {
            Exercise exercise = exerciseMap.get(answerRequest.getIdExercise());
            ScoreRes.ExerciseResult result = new ScoreRes.ExerciseResult();
            result.setExerciseId(answerRequest.getIdExercise());
            result.setUserAnswer(answerRequest.getAnswer());

            if (exercise == null) {
                result.setCorrect(false);
            } else {
                boolean isCorrect = isCorrectAnswer(exercise, answerRequest.getAnswer());
                result.setCorrect(isCorrect);
                if (isCorrect) {
                    answerCorrect++;
                }
            }
            results.add(result);
        }

        float score = answerCorrect * soNhan;

        ScoreRes scoreRes = new ScoreRes();
        scoreRes.setScore(score);
        scoreRes.setMessage(String.format("You scored %.2f points out of %d exercises.", score, exercises.size()));
        scoreRes.setResults(results);
        return scoreRes;
    }

    private boolean isCorrectAnswer(Exercise exercise, String userAnswer) {
        if (userAnswer == null || !List.of("A", "B", "C", "D").contains(userAnswer.toUpperCase())) {
            return false;
        }
        String correctAnswer = exercise.getDapAnDung().name();
        return correctAnswer.equalsIgnoreCase(userAnswer);
    }

    public List<Exercise> findExerciseListRandomByLessonId(long idLesson, int randomNumber) {
        List<Exercise> exerciseList = exerciseRepository.findAllByLessonId(idLesson);
        Collections.shuffle(exerciseList);

        if (exerciseList.size() >= randomNumber) {
            return new ArrayList<>(exerciseList.subList(0, randomNumber));
        } else {
            return new ArrayList<>(exerciseList);
        }
    }

}