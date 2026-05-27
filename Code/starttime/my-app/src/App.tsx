import React from "react";
import CourseCard from "./CourseCard";
import Button from "./Button";

const courses = [
  { id: 1, name: "Artificial Intelligence" },
  { id: 2, name: "Cybersecurity" },
  { id: 3, name: "Data Science" },
];

const userId = 123; // Dummy user ID

function App() {
  return (
    <div className="h-screen flex flex-col justify-center items-center space-y-4">
      {courses.map((course) => (
        <CourseCard
          key={course.id}
          courseId={course.id}
          courseName={course.name}
          userId={userId}
        />
      ))}

      {/* Button for AI Course */}
      <Button
        link="https://skillsbuild.org/students/course-catalog/artificial-intelligence"
        text="Go to AI"
      />
    </div>
  );
}

export default App;
