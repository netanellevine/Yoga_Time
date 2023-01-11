import firebase_admin
from firebase_admin import credentials, firestore
import json


class data:

    def __init__(self, debug=False) -> None:
        cred = credentials.Certificate("./service.json")
        firebase_admin.initialize_app(cred)
        self.db: firestore.firestore.Client = firestore.client()
        self.debug = debug
        if(self.debug):
            self.debug_print("Successfully connected")

    def debug_print(self, printstr):
        if self.debug:
            print(printstr)

    def instructor_exists(self, user_id):
        res = self.db.collection('Instructors').document(user_id).get().to_dict()
        self.debug_print(f"Instructor exists activated returned {res is not None}")
        return res is not None

    def create_instructor(self, user_id, first_name, last_name, work_place):
        dict_to_add = {
            "userId": user_id,
            "firstName": first_name,
            "lastName": last_name,
            "workPlace": work_place,
        }
        res = self.db.collection('Instructors').document(user_id).set(dict_to_add)
        self.debug_print(f"Added instructor {user_id} successfully")
        return res is not None

    def participant_exists(self, user_id):
        res = self.db.collection('Participants').document(user_id).get().to_dict()
        self.debug_print(f"Participants exists activated returned {res is not None}")
        return res is not None

    def add_participant(self, user_id, first_name, last_name):
        dict_to_add = {
            "userId": user_id,
            "firstName": first_name,
            "lastName": last_name,
        }
        res = self.db.collection('Participants').document(user_id).set(dict_to_add)
        self.debug_print(f"Added participant {user_id} successfully")
        return res is not None

    def getInstructorTimeFromDatabase(self, user_id):
        res = self.db.collection("Lessons").document(user_id).get().to_dict()
        self.debug_print(res)
        return res

    def addUserToLesson(self, userId, key, lesson, userToAdd):
        self.debug_print(f'\nuserId: {userId}, \nkey: {key}, \nlesson: {lesson}, \nuserToAdd: {userToAdd}\n')
        if userToAdd not in lesson['ParticipantsList'] and len(lesson['ParticipantsList']) < lesson['maxNumberOfParticipants']:
            lesson['ParticipantsList'].append(userToAdd)
            dict_to_add = {key: json.dumps(lesson)}
            res = self.db.collection("Lessons").document(userId).set(dict_to_add, merge=True)
            #after_dict = self.db.collection("Lessons").document(userId).get().to_dict()
            #print(json.loads(after_dict[key]))
            #print(type(json.loads(after_dict[key])))
            after_dict = json.loads(self.db.collection("Lessons").document(userId).get().to_dict()[key])
            ans = {}
            if userToAdd in after_dict['ParticipantsList']:
                ans['status'] = 200
                ans['result'] = True
                ans['message'] = 'User added successfully'
                self.debug_print(ans)
                return True
            ans['status'] = 404
            ans['result'] = False
            ans['message'] = 'Failed to add user'
            self.debug_print(ans)
            return False

    def removeUserFromLesson(self, userId, key, lesson, userToAdd):
        lesson['ParticipantsList'].remove(userToAdd)
        dict_to_add = {key: json.dumps(lesson)}
        self.db.collection("Lessons").document(userId).set(dict_to_add, merge=True)
        self.debug_print("Removed successfully")

    def addLesson(self, userId, dict_to_add):
        self.db.collection("Lessons").document(userId).set(dict_to_add, merge=True)
        self.debug_print("Added succesfully")

    def validateLesson(self, userId, key, lesson_to_add):
        dict_to_add = {key: json.dumps(lesson_to_add)}
        lesson_list = self.db.collection('Lessons').document(userId).get().to_dict().keys()
        ans = True
        for lesson in lesson_list:
            if (compare_keys(key, lesson)):
                ans = False
        if ans:
            self.addLesson(userId, dict_to_add)
            self.debug_print("Added successfully")
        else:
            self.debug_print("Couldn't add to lesson")
        return ans

    def getAvailability(self, userId, date):
        docs = self.db.collection("Lessons").get()
        res = []
        for doc in docs:
            for key, value in doc.to_dict().items():
                if date in key:
                    value = json.loads(value)
                    if userId in value['ParticipantsList'] or \
                            len(value['ParticipantsList']) < value['maxNumberOfParticipants']:
                                res.append({'doc_id': doc.id, 'date': key, 'lesson': value})
        res.sort(key=lambda x: list(x.keys())[0])
        return res


def compare_time(start, end, start_comp, end_comp):
    return start < start_comp < end_comp or start < end_comp < end \
        or start_comp < start < end_comp or start_comp < end < end_comp


def compare_keys(key, key_comp):
    if key == key_comp:
        return True
    key = key.split('_')
    key_comp = key_comp.split('_')
    if key[0] == key_comp[0]:
        start, end = key[1].split('-')
        start_comp, end_comp = key_comp[1].split('-')
        return compare_time(start, end, start_comp, end_comp)
    return False 
