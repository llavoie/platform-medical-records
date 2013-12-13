(function () {
    'use strict';

    var widgetModule = angular.module('motech-mrs');

    widgetModule.directive('innerlayout', function() {
        return {
            restrict: 'EA',
            link: function(scope, elm, attrs) {
                var eastSelector;
                /*
                * Define options for inner layout
                */
                scope.innerLayoutOptions = {
                    name: 'innerLayout',
                    //resizeWithWindowDelay: 250, // delay calling resizeAll when window is *still* resizing
                    // resizeWithWindowMaxDelay: 2000, // force resize every XX ms while window is being resized
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 6,
                    spacing_closed: 30,
                    //south__showOverflowOnHover: true,
                    center__showOverflowOnHover: true,
                    east__size: 300,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_open: '',
                    togglerContent_closed: '<div><i class="icon-caret-left button"></i></div>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 0,
                    togglerLength_closed: 35,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    east__initClosed: true,
                    initHidden: true
                    //isHidden: true
                };

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                scope.innerLayout = elm.layout(scope.innerLayoutOptions);

                // BIND events to hard-coded buttons
                //scope.innerLayout.addCloseBtn( "#tbarCloseEast", "east" );
            }
        };
    });

    widgetModule.directive('datepicker', function() {

        var momentDateFormat = 'DD/MM/YYYY';

        function fromFormattedDate(formattedDate) {
            return moment(parseInt(moment(formattedDate, momentDateFormat).valueOf().toString(), 10)).format();
        }

        function toFormattedDate(startTimeInMillis) {
            if (startTimeInMillis === null) {
                return null;
            } else if (typeof(startTimeInMillis) !== 'undefined') {
                return moment(parseInt(startTimeInMillis, 10)).format(momentDateFormat);
            }
        }

        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                ngModel.$formatters.push(toFormattedDate);
                ngModel.$parsers.push(fromFormattedDate);
                element.datepicker({
                    dateFormat: 'dd/mm/yy',
                    changeMonth: true,
                    changeYear: true,
                    yearRange: "-120:-0",
                    maxDate: +0,
                    onSelect: function(formattedDate) {
                        ngModel.$setViewValue(element.val());
                        scope.$apply();
                    }
                });
            }
        };
    });
}());
