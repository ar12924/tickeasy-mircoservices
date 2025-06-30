var change_list_button_el=document.querySelector(".change_list");
var dist_list_button_el=document.querySelector(".dist_list");
const tbody_el = document.querySelector("tbody");


document.addEventListener("click",function(e){
    if(e.target.classList.contains("change_list")){
        
        
        change_list_button_el.classList.add("on");
        dist_list_button_el.classList.remove("on");
        
    }else if(e.target.classList.contains("dist_list")){
        change_list_button_el.classList.remove("on");
        dist_list_button_el.classList.add("on");
    }
    
})



document.getElementById('datatable_search').addEventListener('input', function () {
    $('#example').DataTable().search(this.value).draw();
  });

  
  function distTicketList_loaded() {
  	
  	tbody_el.innerHTML = "";

  	fetch('/maven-tickeasy-v1/eventdetail/dist-ticket-list', {
  		method: `POST`,
  		headers: { 'Content-Type': 'application/json' },
  		body: JSON.stringify({
  		
  		})
  	})
  		.then(resp => resp.json())
  		.then(distTicketLists => {
  			if(!Array.isArray(distTicketLists)){
  									distTicketLists = [];
  						}
				for (let distTicketList of distTicketLists) {

				

											tbody_el.insertAdjacentHTML("beforeend", `
												<tr>
												                 <td>${distTicketList.distId}</td>
																 <td>${distTicketList.ticketId}</td>
												                 <td>${distTicketList.buyerOrder.memberId}</td>
												                 <td>${distTicketList.receivedMemberId}</td>
												                 <td>${distTicketList.updateTime}</td>
												                 <td>已分票</td>
												               </tr>
								                `)
											
										}
										
										new DataTable('#example', {
										      dom: 't<"table-footer"lip><"clear">',
										      language: {
										        zeroRecords: "查無資料，請重新搜尋~~",
										        emptyTable: "目前尚無任何資料",
										        // 可選：一起改其他提示
										        lengthMenu: "每頁顯示 _MENU_ 筆",
										        info: "顯示第 _START_ 到 _END_ 筆，共 _TOTAL_ 筆資料",
										        infoEmpty: "共 _TOTAL_ 筆資料",
										        infoFiltered: "",
										        paginate: {
										          first: "第一頁",
										          last: "最後一頁",
										          next: "下一頁",
										          previous: "上一頁"
										        }
										      }
										    });
											})
										}
distTicketList_loaded();