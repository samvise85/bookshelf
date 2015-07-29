
window.RestRequest = window.AbstractModel.extend({
  urlRoot: '/analytics/requests'
});
window.RestRequests = window.AbstractCollection.extend({
  url : '/analytics/requests',
  model: RestRequest
});

window.RestError = window.AbstractModel.extend({
  urlRoot: '/analytics/errors'
});
window.RestErrors = window.AbstractCollection.extend({
  url : '/analytics/errors',
  model: RestError
});

window.Label = window.AbstractModel.extend({
  urlRoot: '/labels'
});
window.Labels = window.AbstractCollection.extend({
  url : '/labels',
  model: Label
});

window.Routes = window.AbstractCollection.extend({
  url : '/analytics/routes',
  model: Route
});