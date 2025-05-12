from gradientai import Gradient
import os
from flask import Flask, request, jsonify
import re
import random

# Set your credentials
token = 'LXkZBp5a1nGCP16xM7QieYKNz2Ns0tOW'
workspace_id = 'b5f958d9-ffd4-41bb-a492-704114e02c8e_workspace'

# Environment config
os.environ['GRADIENT_ACCESS_TOKEN'] = token
os.environ['GRADIENT_WORKSPACE_ID'] = workspace_id

app = Flask(__name__)

# Gradient client
gradient = Gradient()

# Dummy interest-based question bank
dummy_questions = {
    "Java": [
        {
            "question": "What is Java?",
            "options": ["A fruit", "A programming language", "A planet", "A city"],
            "correct_answer": "A programming language",
            "topic": "Java"
        },
        {
            "question": "Which keyword is used to inherit a class in Java?",
            "options": ["extends", "inherits", "instanceof", "implements"],
            "correct_answer": "extends",
            "topic": "Java"
        },
        {
            "question": "Which company originally developed Java?",
            "options": ["Oracle", "Sun Microsystems", "Microsoft", "IBM"],
            "correct_answer": "Sun Microsystems",
            "topic": "Java"
        }
    ],
    "Python": [
        {
            "question": "Which symbol starts a comment in Python?",
            "options": ["#", "//", "/*", "--"],
            "correct_answer": "#",
            "topic": "Python"
        },
        {
            "question": "Which function outputs text in Python?",
            "options": ["print()", "output()", "echo()", "say()"],
            "correct_answer": "print()",
            "topic": "Python"
        },
        {
            "question": "What is the correct way to create a list in Python?",
            "options": ["list = {}", "list = []", "list = ()", "list = <>"],
            "correct_answer": "list = []",
            "topic": "Python"
        }
    ],
    "Web Development": [
        {
            "question": "What does HTML stand for?",
            "options": ["Hyper Text Markup Language", "Home Tool Markup Language", "Hyper Tool Multi Language", "Hyperlinks and Text Markup Language"],
            "correct_answer": "Hyper Text Markup Language",
            "topic": "Web Development"
        },
        {
            "question": "What is used to style websites?",
            "options": ["JavaScript", "CSS", "Python", "SQL"],
            "correct_answer": "CSS",
            "topic": "Web Development"
        },
        {
            "question": "Which language makes websites interactive?",
            "options": ["CSS", "JavaScript", "HTML", "XML"],
            "correct_answer": "JavaScript",
            "topic": "Web Development"
        }
    ],
    "AI": [
        {
            "question": "What is the full form of AI?",
            "options": ["Automated Interaction", "Artificial Intelligence", "Analytical Interface", "Artificial Integration"],
            "correct_answer": "Artificial Intelligence",
            "topic": "AI"
        },
        {
            "question": "Which of these is a subset of AI?",
            "options": ["Machine Learning", "Web Development", "Database Design", "Data Entry"],
            "correct_answer": "Machine Learning",
            "topic": "AI"
        },
        {
            "question": "What is an example of AI in everyday life?",
            "options": ["Smartphone voice assistant", "Textbook", "Mouse", "Paper"],
            "correct_answer": "Smartphone voice assistant",
            "topic": "AI"
        }
    ],
    "Algorithms": [
        {
            "question": "What is the time complexity of binary search?",
            "options": ["O(n)", "O(log n)", "O(n^2)", "O(1)"],
            "correct_answer": "O(log n)",
            "topic": "Algorithms"
        },
        {
            "question": "Which algorithm is used for sorting?",
            "options": ["Merge Sort", "Linear Search", "Dijkstra's", "BFS"],
            "correct_answer": "Merge Sort",
            "topic": "Algorithms"
        },
        {
            "question": "Which algorithm finds shortest paths in a graph?",
            "options": ["DFS", "Prim's", "Dijkstra's", "Quick Sort"],
            "correct_answer": "Dijkstra's",
            "topic": "Algorithms"
        }
    ],
    "Data Structures": [
        {
            "question": "Which data structure uses FIFO?",
            "options": ["Queue", "Stack", "Tree", "Graph"],
            "correct_answer": "Queue",
            "topic": "Data Structures"
        },
        {
            "question": "Which data structure is used in recursion?",
            "options": ["Queue", "Array", "Stack", "Linked List"],
            "correct_answer": "Stack",
            "topic": "Data Structures"
        },
        {
            "question": "Which data structure has nodes and children?",
            "options": ["Tree", "Array", "Stack", "Queue"],
            "correct_answer": "Tree",
            "topic": "Data Structures"
        }
    ],
    "UI/UX": [
        {
            "question": "What does UX stand for?",
            "options": ["User Experience", "User Expert", "Unified Experience", "Universal Experience"],
            "correct_answer": "User Experience",
            "topic": "UI/UX"
        },
        {
            "question": "Which tool is commonly used to design wireframes?",
            "options": ["Figma", "MySQL", "IntelliJ", "Photoshop"],
            "correct_answer": "Figma",
            "topic": "UI/UX"
        },
        {
            "question": "What is the goal of UX design?",
            "options": ["User satisfaction", "Backend performance", "Styling", "Security"],
            "correct_answer": "User satisfaction",
            "topic": "UI/UX"
        }
    ],
    "Databases": [
        {
            "question": "What does SQL stand for?",
            "options": ["Structured Query Language", "Simple Query Logic", "Sequential Query Language", "System Query Layer"],
            "correct_answer": "Structured Query Language",
            "topic": "Databases"
        },
        {
            "question": "Which of the following is a NoSQL database?",
            "options": ["MongoDB", "MySQL", "PostgreSQL", "Oracle"],
            "correct_answer": "MongoDB",
            "topic": "Databases"
        },
        {
            "question": "Which SQL command is used to retrieve data?",
            "options": ["GET", "SELECT", "SHOW", "FETCH"],
            "correct_answer": "SELECT",
            "topic": "Databases"
        }
    ],
    "Testing": [
        {
            "question": "What is unit testing?",
            "options": ["Testing individual units/components", "Testing UI", "Testing the whole system", "Performance testing"],
            "correct_answer": "Testing individual units/components",
            "topic": "Testing"
        },
        {
            "question": "Which is a common testing framework in Java?",
            "options": ["JUnit", "Mocha", "RSpec", "NUnit"],
            "correct_answer": "JUnit",
            "topic": "Testing"
        },
        {
            "question": "Which type of testing ensures software performs under load?",
            "options": ["Load testing", "Unit testing", "UI testing", "Beta testing"],
            "correct_answer": "Load testing",
            "topic": "Testing"
        }
    ],
    "Cybersecurity": [
        {
            "question": "What is phishing?",
            "options": ["A hacking technique", "A programming error", "A database type", "A security patch"],
            "correct_answer": "A hacking technique",
            "topic": "Cybersecurity"
        },
        {
            "question": "What is a strong password?",
            "options": ["123456", "admin", "P@ssw0rd!", "password"],
            "correct_answer": "P@ssw0rd!",
            "topic": "Cybersecurity"
        },
        {
            "question": "Which of the following is a common cybersecurity threat?",
            "options": ["Malware", "Bluetooth", "SSD", "Keyboard"],
            "correct_answer": "Malware",
            "topic": "Cybersecurity"
        }
    ]
}



def fetchQuizFromLlama(student_topic):
    print("Fetching quiz from llama")
    base_model = gradient.get_base_model(base_model_slug="llama3-8b-chat")
    query = (
        f"[INST] Generate a quiz with 3 questions to test students on the provided topic. "
        f"For each question, generate 4 options where only one of the options is correct. "
        f"Format your response as follows:\n"
        f"QUESTION: [Your question here]?\n"
        f"OPTION A: [First option]\n"
        f"OPTION B: [Second option]\n"
        f"OPTION C: [Third option]\n"
        f"OPTION D: [Fourth option]\n"
        f"ANS: [Correct answer letter]\n\n"
        f"Ensure proper formatting. Topic:\n{student_topic}"
        f"[/INST]"
    )
    response = base_model.complete(query=query, max_generated_token_count=500).generated_output
    return response

def process_quiz(text, fallback_topic="General"):
    questions = []
    pattern = re.compile(
        r'QUESTION: (.*?)\nOPTION A: (.*?)\nOPTION B: (.*?)\nOPTION C: (.*?)\nOPTION D: (.*?)\nANS: (.)',
        re.DOTALL
    )
    matches = pattern.findall(text)

    for match in matches:
        question, a, b, c, d, ans = match
        options = [a.strip(), b.strip(), c.strip(), d.strip()]
        correct_answer = options[ord(ans.upper()) - ord('A')]
        random.shuffle(options)
        questions.append({
            "question": question.strip(),
            "options": options,
            "correct_answer": correct_answer,
            "topic": fallback_topic
        })

    return questions

@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    topic = request.args.get('topic')
    if not topic:
        return jsonify({'error': 'No topic provided'}), 400

    try:
        raw_quiz = fetchQuizFromLlama(topic)
        return jsonify({'quiz': process_quiz(raw_quiz, topic)})

    except Exception as e:
        print(f"Gradient failed, returning dummy quiz: {e}")
        selected_interests = [i.strip() for i in topic.split(',')]
        fallback_questions = []

        for interest in selected_interests:
            if interest in dummy_questions:
                for q in dummy_questions[interest]:
                    q_with_topic = q.copy()
                    q_with_topic["topic"] = interest
                    options = q_with_topic["options"]
                    correct = q_with_topic["correct_answer"]

                    # Shuffle options and realign correct answer
                    random.shuffle(options)
                    q_with_topic["options"] = options
                    for opt in options:
                        if opt == correct:
                            q_with_topic["correct_answer"] = opt
                            break

                    fallback_questions.append(q_with_topic)

        random.shuffle(fallback_questions)
        quiz = fallback_questions[:6]
        return jsonify({'quiz': quiz}), 200

if __name__ == '__main__':
    app.run(port=5001, host='0.0.0.0')
