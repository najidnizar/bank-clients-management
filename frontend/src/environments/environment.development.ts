// Development build (`ng serve`): replaces environment.ts (see fileReplacements in angular.json).
// Keycloak runs in Docker Compose on port 8180.
export const environment = {
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'bank',
    clientId: 'bank-frontend',
  },
};
