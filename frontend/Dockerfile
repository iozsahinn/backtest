# Stage 1: Build Angular app
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

# Stage 2: Serve with nginx
FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/nginx.conf /etc/nginx/nginx.conf

COPY --from=build /app/dist/optimizer-fe /usr/share/nginx/html
EXPOSE 80
