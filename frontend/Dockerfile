FROM node:20 AS build

WORKDIR /app
COPY . .

ARG VITE_BASE_URL
ARG VITE_BACKEND_URL
ENV VITE_BASE_URL=${VITE_BASE_URL}
ENV VITE_BACKEND_URL=${VITE_BACKEND_URL}

RUN npm install && npm run build

FROM node:20-alpine

WORKDIR /app
COPY --from=build /app/dist /app/dist

RUN npm install -g serve
CMD ["serve", "-s", "dist", "-l", "3000"]

#FROM node:20 AS build
#
#WORKDIR /app
#COPY . .
#
#ARG VITE_BASE_URL
#ARG VITE_BACKEND_URL
#ENV VITE_BASE_URL=${VITE_BASE_URL}
#ENV VITE_BACKEND_URL=${VITE_BACKEND_URL}
#
#RUN npm install && npm run build
#
#FROM nginx:stable-alpine
#
#COPY --from=build /app/dist /usr/share/nginx/html
#
#COPY nginx.conf /etc/nginx/conf.d/default.conf