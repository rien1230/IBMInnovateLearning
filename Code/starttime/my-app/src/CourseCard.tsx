import React from "react";

interface CourseCardProps {
  courseId: number;
  courseName: string;
  userId: number;
}

const CourseCard: React.FC<CourseCardProps> = ({ courseId, courseName, userId }) => {
  const handleStartClick = async () => {
    try {
      // Step 1: Call backend to record start time
      const response = await fetch(`http://localhost:8080/api/courses/start?userId=${userId}&courseId=${courseId}`, {
        method: "POST",
      });

      if (response.ok) {
        console.log("Start time recorded!");

        // Step 2: Redirect to course URL
        const courseUrl = `https://skillsbuild.org/students/course-catalog/${courseName}`;
        window.location.href = courseUrl;
      } else {
        console.error("Failed to record start time");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <div className="bg-blue-400 ">
      <h3>{courseName}</h3>
      <button onClick={handleStartClick}>Start</button>
    </div>
  );
};

export default CourseCard;
