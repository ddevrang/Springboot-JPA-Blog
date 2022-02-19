let index = {
	init: function(){
		$("#btn-save").on("click", ()=>{ // function(){}을 사용하지 않고, ()=>{}를 사용하는 이유는 this를 바인딩하기 위해서!! 
			this.save();
		});
		$("#btn-update").on("click", ()=>{ // function(){}을 사용하지 않고, ()=>{}를 사용하는 이유는 this를 바인딩하기 위해서!! 
			this.update();
		});

//  	스프링 시큐리티 로그인을 사용할 것이므로 이 로직은 필요없음. (전통적인 로그인 방식임)
//		$("#btn-login").on("click", ()=>{ // function(){}을 사용하지 않고, ()=>{}를 사용하는 이유는 this를 바인딩하기 위해서!! 
//			this.login();
//		});
	},
		
	save: function(){
			// alert('user의 save함수 호출됨');
			let data = {
				username: $("#username").val(),
				password: $("#password").val(),
				email: $("#email").val()
			};
			
			// console.log(data);
			
			// ajax호출시 default가 비동기 호출
			// ajax 통신을 이용해서 3개의 데이터를 json으로 변경하여 insert 요청!!
			// ajax가 통신을 성공하고 서버가 json을 리턴해주면 자동으로 자바 오브젝트로 변환해줌. 
			//    => dataType을 생략해도  jQuery가 MIME 타입 등을 보면서 자동으로 결정해서 처리해준다.
			$.ajax({ 
				type: "POST",
				url: "/auth/joinProc",
				data: JSON.stringify(data), // http body 데이터. js 오브젝트로 보내면 java가 이해하지못하기에 json으로 변환하여 보냄.
				contentType: "application/json; charset=utf-8", // body데이터가 어떤 타입인지(MIME)
				dataType: "json" // 요청을 서버로해서 응답이 왔을 때, 기본적으로 모든 것이 문자열(String). ※ 100% 문자열은 아닌데 대부분 문자열.
				                             // 이 때 그 문자열의 형식이 json이라면 => javascript 오브젝트로 변경되도록 함.
				                             // dataType은 서버에서 반환되는 데이터 형식을 지정하는 것.
				
			}).done(function(resp){	// ajax 요청에 대한 응답의 결과가 정상이면 done을 실행
				if(resp.status === 500){
					alert("회원가입에 실패하였습니다.");
				}else{
					alert("회원가입이 완료되었습니다.");
					//console.log(resp);
					location.href = "/";
				}

			}).fail(function(error){ // ajax 요청이 실패하면 fail을 실행
				alert(JSON.stringify(error));
			}); 
	},
	
	update: function(){

			let data = {
				id: $("#id").val(),
				username: $("#username").val(),
				password: $("#password").val(),
				email: $("#email").val()
			};
			
			$.ajax({ 
				type: "PUT",
				url: "/user",
				data: JSON.stringify(data),
				contentType: "application/json; charset=utf-8",
				dataType: "json"
			}).done(function(resp){
					alert("회원수정이 완료되었습니다.");
					location.href = "/";
			}).fail(function(error){
				alert(JSON.stringify(error));
			}); 
	}
	
// 스프링 시큐리티 로그인을 사용할 것이므로 이 로직은 필요없음. (전통적인 로그인 방식임)
//login: function(){
//	let data = {
//		username: $("#username").val(),
//		password: $("#password").val()
//	};
//	
//	$.ajax({ 
//		type: "POST",	// 주소에 데이터가 보여지는 GET방식으로 로그인을 요청하는 것은 매우 위험하므로 POST방식을 사용
//		url: "/api/user/login",
//		data: JSON.stringify(data),
//		contentType: "application/json; charset=utf-8",
//		dataType: "json"
//	}).done(function(resp){
//			alert("로그인이 완료되었습니다.");
//			location.href = "/";
//	}).fail(function(error){
//		alert(JSON.stringify(error));
//	}); 
//	
//}
	
}

index.init();