$(document).ready(function() {
    // 타이틀 클릭 시, 상세 페이지로 이동
    $(".simulation-date").on("click", function() {
    	$("#simulationId").val($(this).data("simulation-id"));
    	$("#dataForm").submit(); // 페이지 이동
    });
    
    $('#enter-room-btn').click(function () {
    	const roomId = $('#roomIdInput').val().trim();
    	
    	if (roomId === "") {
            alert("방 ID를 입력해주세요.");
            return;
        }
    	
    	$.ajax({
            url: '/simulations/exists',
            method: 'POST',
            data: { roomId: roomId },
            success: function (response) {
                if (response) {
                	$('#hiddenPostForm').submit();
                } else {
                    // 방이 없으면 방을 만들지 물어보기
                    const createRoom = confirm("해당 방이 없습니다. 방을 생성하시겠습니까?");
                    if (createRoom) {
                    	$('#hiddenPostForm').submit();
                    } else {
                        alert("방 생성이 취소되었습니다.");
                    }
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });
    
    $('.continue-btn').on('click', function () {
        var simulationId = $(this).data('simulation-id');
        $('#simulationIdInput').val(simulationId);
        $('#continueRoomModal').fadeIn(200);
        $('#continueRoomIdInput').val('').focus();
    });
    
    $('#continueRoomEnterBtn').on('click', function () {
        const roomId = $('#continueRoomIdInput').val().trim();
        if (!roomId) {
            alert("방 ID를 입력해주세요.");
            return;
        }
        
        $.ajax({
            url: '/simulations/exists',
            method: 'POST',
            data: { roomId: roomId },
            success: function (response) {
                if (response) {
                	const createRoom = confirm("이미 사용중인 방입니다. 참가하시겠습니까?");
                	if(createRoom){
                    	$('#roomIdInput').val(roomId);
                    	$('#hiddenPostForm').submit();
                	}
                } else {
                	$('#roomIdInput').val(roomId);
                	$('#hiddenPostForm').submit();
                }
            },
            error: function () {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });

    // 취소 버튼 클릭 → 모달 닫기
    $('#continueRoomCancelBtn').on('click', function () {
        $('#continueRoomModal').fadeOut(200);
        selectedSimulationId = null;
    });
});
