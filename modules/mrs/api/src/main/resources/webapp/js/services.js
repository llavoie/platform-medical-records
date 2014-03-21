(function () {
    'use strict';
    /* Services */

    angular.module('mrs.services', ['ngResource']).factory('Patient', function ($resource) {
        return $resource('../mrs/api/patients/:motechId', {motechId:'@motechId'}, {
            update: { method: 'PUT' }
        });
    });
}());
