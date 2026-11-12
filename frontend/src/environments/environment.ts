// Production build (`ng build`): values for the Kubernetes deployment, where Keycloak is exposed
// by the Gateway API at keycloak.localhost.
export const environment = {
  keycloak: {
    url: 'http://keycloak.localhost',
    realm: 'bank',
    clientId: 'bank-frontend',
  },
};
