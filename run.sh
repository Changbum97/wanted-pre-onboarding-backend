docker build -t wanted-pre-onboarding-backend .

docker run -p 8080:8080 \
-d wanted-pre-onboarding-backend