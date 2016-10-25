$('#dynaTable').bind('dynatable:init', function (e, dynatable) {
    $('.dynatable-search').wrapAll('<div class="row m-b-sm"><div class="col-md-12" id="opopop"/></div>');
    $('.dynatable-paginate, .dynatable-record-count').wrapAll('<div class="col-md-12"/>');

    $('.dynatable-search').addClass('col-md-2');
    $('.dynatable-search').find('input')
            .addClass('form-control input-sm')
            .attr('placeholder', 'Buscar');

});

$('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
    $('.dynatable-paginate li').first().remove();
    $('.dynatable-search').addClass('text-white');
});

$.dynatableSetup({
    inputs: {
        paginationClass: 'pagination dynatable-paginate pull-right',
        paginationActiveClass: 'active',
        paginationDisabledClass: 'disabled',
        paginationPrev: 'Anterior',
        paginationNext: 'Siguiente',
        recordCountPageBoundTemplate: '{pageLowerBound} al {pageUpperBound} de',
        recordCountPageUnboundedTemplate: '{recordsShown} de',
        recordCountTotalTemplate: '{recordsQueryCount}',
        recordCountFilteredTemplate: ' (Filtrados de un total de {recordsTotal} registros)',
        recordCountText: '',
        recordCountTextTemplate: '{text} {pageTemplate} {totalTemplate} {filteredTemplate}'
    },
    features: {
        sort: false,
        perPageSelect: false
    },
    params: {
        records: 'data',
        totalRecordCount: 'total',
        queryRecordCount: 'filtered'
    },
    dataset: {
        ajax: true,
        ajaxOnLoad: true,
        ajaxDataType: 'json',
        ajaxMethod: 'POST',
        records: [],
        perPageDefault: 8
    },
    table: {
        bodyRowSelector: 'div'
    }
});