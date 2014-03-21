(function () {
    'use strict';

    /* App Module */

    angular.module('mrs', ['motech-dashboard', 'mrs.controllers', 'mrs.directives', 'mrs.services', 'ngCookies', 'ngRoute', 'motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/mrs/patients', {templateUrl: '../mrs/resources/partials/patients.html', controller: 'PatientMrsCtrl'}).
                when('/mrs/patients/:motechId', {templateUrl: '../mrs/resources/partials/patients.html', controller: 'PatientMrsCtrl'}).
                when('/mrs/settings', {templateUrl: '../mrs/resources/partials/settings.html', controller: 'SettingsMrsCtrl'}).
                when('/mrs/mrs/new', {templateUrl: '../mrs/resources/partials/form.html', controller: 'ManagePatientMrsCtrl'}).
                when('/mrs/mrs/:motechId/edit', {templateUrl: '../mrs/resources/partials/form.html', controller: 'ManagePatientMrsCtrl'}).
                when('/mrs/mrs/:motechId/editAttributes', {templateUrl: '../mrs/resources/partials/attributes.html', controller: 'ManagePatientMrsCtrl'});
        }]);
}());
