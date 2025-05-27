$(function() {
    const CONFIG = {
        perPage: 12,
        lowEnrollmentThreshold: 6,
        ajaxEndpoint: 'academico/programacion/resumen/list',
        statsEndpoint: 'academico/programacion/resumen/stats'
    };

    function getCurrentFilters() {
        const departamentoSuperior = $("#departamentoSuperiorSelect").val();
        const filters = {};

        if (departamentoSuperior) {
            filters.departamentoSuperior = departamentoSuperior;
        }

        return filters;
    }

    loadGeneralStats();

    function loadGeneralStats() {
        const filters = getCurrentFilters(); // ✅ obtener filtros actuales

        $.ajax({
            url: APP.url(CONFIG.statsEndpoint),
            type: 'GET',
            data: filters, // ✅ enviar los filtros como parámetros GET
            dataType: 'json',
            beforeSend: function () {
                $("#statsLoadingIndicator").show();
            },
            success: function (response) {
                updateStatsOverview(response);
                console.log(response);
            },
            error: function (xhr, status, error) {
                console.error("Error al cargar estadísticas:", error);
                $(".stats-overview").html(
                    '<div class="alert alert-danger">' +
                    '<i class="fa fa-exclamation-circle"></i> ' +
                    'No se pudieron cargar las estadísticas. Intente de nuevo más tarde.' +
                    '</div>'
                );
            },
            complete: function () {
                $("#statsLoadingIndicator").hide();
            }
        });
    }


    function updateStatsOverview(data) {
        console.log(data);
        animateCounter("#totalCursosValue", data.total || 0);
        animateCounter("#cursosActivosValue", data.activos || 0);
        animateCounter("#cursosBloqueadosValue", data.bloqueados || 0);
        animateCounter("#cursosAnuladosValue", data.anulados || 0);
        animateCounter("#cursosSinDocenteValue", data.cursosSinDocente || 0);
        animateCounter("#cursosFusionadosValue",data.fusionados || 0);
        animateCounter("#cursosMenosAlumnosValue",data.cursosMenosAlumnos || 0);

        $("#lastUpdated").text(formatDateTime(new Date()));
    }

    function animateCounter(selector, targetValue) {
        const $element = $(selector);
        const startValue = parseInt($element.text()) || 0;

        $({counter: startValue}).animate({
            counter: targetValue
        }, {
            duration: 1000,
            easing: 'swing',
            step: function() {
                $element.text(Math.ceil(this.counter));
            },
            complete: function() {
                $element.text(targetValue);
            }
        });
    }

    function formatDateTime(date) {
        return date.toLocaleString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    const dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url(CONFIG.ajaxEndpoint),
            perPageDefault: CONFIG.perPage,
            ajaxOnLoad: true
        },
        features: {
            paginate: true,
            search: true,
            recordCount: true
        },
        writers: {
            _rowWriter: renderDepartmentCard
        },
        params: {
            records: 'data'
        }
    }).data('dynatable');

    function renderDepartmentCard(rowIndex, record, columns, cellWriter) {
        record.nombreDep = record.nombreDep || 'Departamento sin nombre';

        const numericFields = [
            'cantidadCursos', 'cantidadGrupos', 'cantidadTotal',
            'cantidadActivos', 'cantidadBloqueados', 'cantidadCancelados',
            'cantidadAnulados', 'cantidadFusionados', 'cursosMenosAlumnos',
            'cursosSinDocente'
        ];

        numericFields.forEach(field => {
            record[field] = parseInt(record[field] || 0);
        });

        return $.templates("#templateResumx").render(record);
    }

    $(document).on("click", ".department-card", function() {
        const id = $(this).attr("rel");
        if (id) {
            location.href = APP.url(`academico/programacion/resumen/${id}/departamento`);
        }
    });

    $(document).on("mouseenter", ".department-card", function() {
        $(this).css("box-shadow", "0 5px 15px rgba(0,0,0,0.2)");
        $(this).css("cursor", "pointer");
    }).on("mouseleave", ".department-card", function() {
        $(this).css("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");
    });

    $("#refreshData").on("click", function() {
        loadGeneralStats();

        dynatable.settings.dataset.ajaxData = {};
        dynatable.process();

        $("#loadingIndicator").show();

        $(document).ajaxComplete(function(event, xhr, settings) {
            if (settings.url.includes(CONFIG.ajaxEndpoint)) {
                $("#loadingIndicator").hide();
            }
        });
    });

    $("#refreshStats").on("click", function() {
        loadGeneralStats();
    });

    $("#filterSelect").on("change", function() {
        const filterValue = $(this).val();

        if (filterValue === "all") {
            dynatable.settings.dataset.queries = {};
            console.log("holaaaaaaaaaaaaa");
            console.log(dynatable.settings.dataset);
        } else {
            dynatable.settings.dataset.queries = { filter: filterValue };
        }

        dynatable.process();
    });

    $("#departamentoSuperiorSelect").on("change", function () {
        const selected = $(this).val();

        const queries = dynatable.settings.dataset.queries || {};

        if (selected) {
            queries.departamentoSuperior = selected;
        } else {
            delete queries.departamentoSuperior;
        }

        dynatable.settings.dataset.queries = queries;
        dynatable.process();

        loadGeneralStats();
    });

});