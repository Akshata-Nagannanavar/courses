package com.example.course_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;

    public DataSeeder(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        long existingCount = courseRepository.count();
        System.out.println("🔍 [DataSeeder] Checking database... Found " + existingCount + " existing courses.");

        if (existingCount == 0) {
            System.out.println("🌱 [DataSeeder] No courses found. Seeding sample data...");

            List<Course> courses = List.of(
                    new Course(null,
                            "Mathematics Basics",
                            "Covers numbers, addition, subtraction and multiplication.",
                            Board.CBSE,
                            List.of(Subject.MATHEMATICS),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_1),
                            null),

                    new Course(null,
                            "Science Starter",
                            "Introduction to physics, chemistry, and biology concepts.",
                            Board.STATE,
                            List.of(Subject.SCIENCE),
                            List.of(Medium.ENGLISH, Medium.KANNADA),
                            List.of(Grade.CLASS_2),
                            null),

                    new Course(null,
                            "English Grammar Essentials",
                            "Learn basic grammar, tenses, and sentence structure.",
                            Board.ICSE,
                            List.of(Subject.ENGLISH),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_3),
                            null),

                    new Course(null,
                            "History and Geography",
                            "Explore ancient civilizations and Indian geography.",
                            Board.STATE,
                            List.of(Subject.SOCIAL),
                            List.of(Medium.KANNADA, Medium.ENGLISH),
                            List.of(Grade.CLASS_4, Grade.CLASS_5),
                            null),

                    new Course(null,
                            "Hindi Literature",
                            "Enhance Hindi reading and writing with stories and poems.",
                            Board.ICSE,
                            List.of(Subject.HINDI),
                            List.of(Medium.HINDI),
                            List.of(Grade.CLASS_5, Grade.CLASS_6),
                            null),

                    new Course(null,
                            "Kannada Language",
                            "Learn Kannada grammar and vocabulary through simple lessons.",
                            Board.STATE,
                            List.of(Subject.KANNADA),
                            List.of(Medium.KANNADA),
                            List.of(Grade.CLASS_2, Grade.CLASS_3),
                            null),

                    new Course(null,
                            "Environmental Studies",
                            "Learn about nature, seasons, and environmental care.",
                            Board.CBSE,
                            List.of(Subject.SCIENCE),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_1, Grade.CLASS_2),
                            null),

                    new Course(null,
                            "Geometry Fundamentals",
                            "Understand lines, angles, shapes, and basic geometry terms.",
                            Board.ICSE,
                            List.of(Subject.MATHEMATICS),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_6),
                            null),

                    new Course(null,
                            "Physics Essentials",
                            "Covers motion, force, and simple machines with examples.",
                            Board.STATE,
                            List.of(Subject.SCIENCE),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_7, Grade.CLASS_8),
                            null),

                    new Course(null,
                            "Civics and Economics",
                            "Basic introduction to governance, rights, and money systems.",
                            Board.CBSE,
                            List.of(Subject.SOCIAL),
                            List.of(Medium.ENGLISH),
                            List.of(Grade.CLASS_9, Grade.CLASS_10),
                            null)
            );

            courseRepository.saveAll(courses);

            long newCount = courseRepository.count();
            System.out.println("✅ [DataSeeder] Seeded " + courses.size() + " courses successfully.");
            System.out.println("📊 [DataSeeder] Total courses in DB now: " + newCount);
        } else {
            System.out.println("✅ [DataSeeder] Existing data found. Skipping seeding (" + existingCount + " courses).");
        }
    }
}
