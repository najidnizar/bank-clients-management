# Frontend

Angular 22 single-page application of bank-clients-management. Architecture, choices and roadmap: [docs/frontend.md](../docs/frontend.md).

```bash
npm install     # install dependencies
ng serve        # dev server on http://localhost:4200, /api proxied to the gateway (localhost:8088)
ng test         # unit tests (Vitest)
ng build        # production build into dist/
```

The back-end must be running (Docker Compose, profile `apps`): see the [main README](../README.md).
