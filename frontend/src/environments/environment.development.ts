// ng serve (fileReplacements in angular.json): Keycloak from Docker Compose.
export const environment = {
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'bank',
    clientId: 'bank-frontend',
  },
};
