# install production dependencied -> copied into final image
FROM node:17.6.0-alpine AS dev-deps
COPY package.json package-lock.json ./
RUN npm install 

# install dev dependencies
FROM node:17.6.0-alpine AS prod-deps
COPY package.json package-lock.json ./
RUN npm install --production

# run typescript compiler (dev dependencies are needed)
FROM dev-deps AS compile
COPY . ./
RUN npx tsc

# create final production image from compiled ts files and prod dependencies
FROM node:17.6.0-alpine AS final
WORKDIR /app
USER node
COPY --from=prod-deps /node_modules ./node_modules
COPY --from=compile /dist ./dist

# Container info

EXPOSE 3000

CMD [ "node", "dist/index.js" ]