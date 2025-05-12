$(document).ready(function() {
    // 타이틀 클릭 시, 상세 페이지로 이동
    $(".simulation-title").on("click", function() {
    	$("#simulationId").val($(this).data("simulation-id"));
    	$("#dataForm").submit(); // 페이지 이동
    });
});