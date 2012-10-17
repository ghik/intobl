import pymongo

conn = pymongo.Connection()
collection = conn.test.stuff

for i in range(0, 10000):
    object = { "_id": "object"+str(i), "name": "Name"+str(i), "tags": ["tag1", "tag5", "tag"+str(i), "tag"+str(i%5)] }
    collection.insert(object)

for object in collection.find({"tags": "tag3"}):
    print(object)


