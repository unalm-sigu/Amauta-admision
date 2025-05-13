$(function() {
    // Configuration options
    const CONFIG = {
        perPage: 12,
        lowEnrollmentThreshold: 6, // Define what counts as "low enrollment"
        ajaxEndpoint: 'academico/programacion/resumen/list',
        statsEndpoint: 'academico/programacion/resumen/stats' // Nuevo endpoint para estadísticas
    };

    // Cargar estadísticas generales al inicio
    loadGeneralStats();

    // Función para cargar las estadísticas generales
    function loadGeneralStats() {
        $.ajax({
            url: APP.url(CONFIG.statsEndpoint),
            type: 'GET',
            dataType: 'json',
            beforeSend: function() {
                $("#statsLoadingIndicator").show();
            },
            success: function(response) {
                updateStatsOverview(response);
                console.log(response);
            },
            error: function(xhr, status, error) {
                console.error("Error al cargar estadísticas:", error);
                // Mostrar mensaje de error en las estadísticas
                $(".stats-overview").html(
                    '<div class="alert alert-danger">' +
                    '<i class="fa fa-exclamation-circle"></i> ' +
                    'No se pudieron cargar las estadísticas. Intente de nuevo más tarde.' +
                    '</div>'
                );
            },
            complete: function() {
                $("#statsLoadingIndicator").hide();
            }
        });
    }

    // Función para actualizar el panel de estadísticas
    function updateStatsOverview(data) {
        // Actualizar los valores de las estadísticas con animación
        animateCounter("#totalCursosValue", data.activos || 0);
        animateCounter("#cursosActivosValue", data.cancelados || 0);
        animateCounter("#cursosBloqueadosValue", data.bloqueados || 0);
        animateCounter("#otrosEstadosValue", data.anulados || 0);

        // Actualizar fecha de última actualización
        $("#lastUpdated").text(formatDateTime(new Date()));
    }

    // Animación para contadores
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

    // Formato de fecha y hora
    function formatDateTime(date) {
        return date.toLocaleString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Initialize the dynatable with improved settings
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

    // Template renderer with better formatting and data transformation
    function renderDepartmentCard(rowIndex, record, columns, cellWriter) {
        // Apply any data transformations here
        record.nombreDep = record.nombreDep || 'Departamento sin nombre';

        // Ensure all counters are numbers to prevent NaN issues
        const numericFields = [
            'cantidadCursos', 'cantidadGrupos', 'cantidadTotal',
            'cantidadActivos', 'cantidadBloqueados', 'cantidadCancelados',
            'cantidadAnulados', 'cantidadFusionados', 'cursosMenosAlumnos',
            'cursosSinDocente'
        ];

        numericFields.forEach(field => {
            record[field] = parseInt(record[field] || 0);
        });

        // Render the template with the transformed data
        return $.templates("#templateResumx").render(record);
    }

    // Event delegation with more specific selector and improved interaction
    $(document).on("click", ".department-card", function() {
        const id = $(this).attr("rel");
        if (id) {
            location.href = APP.url(`academico/programacion/resumen/${id}/departamento`);
        }
    });

    // Add hover effects for better UX
    $(document).on("mouseenter", ".department-card", function() {
        $(this).css("box-shadow", "0 5px 15px rgba(0,0,0,0.2)");
        $(this).css("cursor", "pointer");
    }).on("mouseleave", ".department-card", function() {
        $(this).css("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");
    });

    // Add refresh button functionality
    $("#refreshData").on("click", function() {
        // Recargar tanto las estadísticas como los datos de la tabla
        loadGeneralStats();

        dynatable.settings.dataset.ajaxData = {};
        dynatable.process();

        // Show loading indicator
        $("#loadingIndicator").show();

        // Hide loading indicator after data loads
        $(document).ajaxComplete(function(event, xhr, settings) {
            if (settings.url.includes(CONFIG.ajaxEndpoint)) {
                $("#loadingIndicator").hide();
            }
        });
    });

    // Refresh solo para estadísticas
    $("#refreshStats").on("click", function() {
        loadGeneralStats();
    });

    // Filter functionality (if needed)
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
    });

});