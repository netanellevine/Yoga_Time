from typing import Union
from fastapi import FastAPI
import data
from pydantic import BaseModel, validator
from datetime import datetime
from enum import Enum

class Instructor(BaseModel):
    userId: str
    firstName: str
    lastName: str
    workPlace: str

class Participant(BaseModel):
    userId: str
    firstName: str
    lastName: str

class LessonLevel(str, Enum):
    All = "All"
    A = "A"
    B = "B"
    C = "C"


# key -> dd/mm/YYYY_HH:MM-HH:MM # date and start and end time
# lesson -> { 
#     "ParticipantsList": list[str],
#     "description": str,
#     "lessonName": str,
#     "level": str,
#     "maxNumberOfParticipants": int,
#     "price": float,
# }

class Lesson(BaseModel):
    ParticipantsList: list[str]
    description: str = ""
    lessonName: str
    level: str
    maxNumberOfParticipants: int
    price: float

class LessonDocument(BaseModel):
    key: str
    lesson: Lesson

app = FastAPI()


@app.get("/")
def root():
    return {"Hello": "World"}

# POST: to create data.
# GET: to read data.
# PUT: to update data.
# DELETE: to delete data.

# def instructor_exists(self, user_id) -> bool: GET
# def create_instructor(self, user_id, first_name, last_name, work_place) -> None: POST
# def participant_exists(self, user_id) -> bool: GET
# def add_participant(self, user_id, first_name, last_name) -> None: POST
# def getInstructorTimeFromDatabase(self, user_id) -> dict: GET
# def addUserToLesson(self, userId, key, lesson, userToAdd) -> None: POST
# def removeUserFromLesson(self, userId, key, lesson, userToAdd) -> None: DELETE
# def validateLesson(self, userId, key, lesson_to_add) -> bool: GET
# def getAvailability(self,userId ,date): GET


dat = data.data(True)

@app.get("/instructor/exists")
def instructor_exists(userId: str) -> bool:
    return dat.instructor_exists(userId)

@app.post("/instructor/create")
def create_instructor(instructor: Instructor) -> bool:
    return dat.create_instructor(instructor.userId, instructor.firstName, instructor.lastName, instructor.workPlace)

@app.get("/participant/exists")
def participant_exists(userId: str) -> bool:
    return dat.participant_exists(userId)

@app.post("/participant/create")
def add_participant(participant: Participant) -> bool:
    return dat.add_participant(participant.userId, participant.firstName, participant.lastName)

@app.get("/instructor/time")
def getInstructorTimeFromDatabase(userId: str) -> dict:
    return dat.getInstructorTimeFromDatabase(userId)

@app.post("/lesson/addUser")
def addUserToLesson(userId: str, key: str, lesson: Lesson, userToAdd: str) -> None:
    return dat.addUserToLesson(userId, key, lesson.dict(), userToAdd)

@app.post("/lesson/removeUser")
def removeUserFromLesson(userId: str, key: str, lesson: Lesson, userToAdd: str) -> None:
    return dat.removeUserFromLesson(userId, key, lesson.dict(), userToAdd)

@app.post("/lesson/validate")
def validateLesson(userId: str, key: str, lesson_to_add: Lesson) -> bool:
    return dat.validateLesson(userId, key, lesson_to_add.dict())

@app.get("/lesson/availability")
def getAvailability(userId: str, date: str) -> list:
    return dat.getAvailability(userId, date)
