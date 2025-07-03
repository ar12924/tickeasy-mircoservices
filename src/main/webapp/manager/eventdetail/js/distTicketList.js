var change_list_button_el=document.querySelector(".change_list");
var dist_list_button_el=document.querySelector(".dist_list");
const tbody_el = document.querySelector("tbody");

const start_el = document.getElementById("start");
const end_el = document.getElementById("end");

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


  
  
  //活動列表
  const select_el=document.getElementsByClassName("form-select")[0];
  function EventListBar_loaded() {



   	fetch('/maven-tickeasy-v1/eventdetail/event-list-bar', {
   		method: `POST`,
   		headers: { 'Content-Type': 'application/json' },
   		body: JSON.stringify({
  		
   		})
   	})
   		.then(resp => resp.json())
   		.then(eventListBars => {
   			if(!Array.isArray(eventListBars)){
   									distTicketLists = [];
   						}
			let isFirst = true;
  			for (let eventListBar of eventListBars) {
				/*if(isFirst){
					const firstEventId=eventListBar.eventId}*/
  			
						
  										select_el.insertAdjacentHTML("beforeend", `
											<option ${isFirst ? "selected" : ""} value="${eventListBar.eventId}">${eventListBar.eventName}</option>
  							                `)
											isFirst = false;
											
  									}
									})
									}
  
  





  //設定一開始load一個月內的資料

  window.addEventListener("DOMContentLoaded", function () {
	EventListBar_loaded();
  	
    const startInput = document.getElementById("start");
    const endInput = document.getElementById("end");

    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1); // 減一個月

    // 補0轉成 yyyy-MM-dd 格式
    const formatDate = (date) => {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    };

    startInput.value = formatDate(oneMonthAgo);
    endInput.value = formatDate(today);
    
    const sIV=startInput.value;
	const eIV=endInput.value;
    const selectValue=select_el.value;
    distTicketList_loaded(sIV,eIV,1);
	changeList(selectValue);
	select_el.addEventListener("change", function () {
				  const selectedId = this.value;
				  distTicketList_loaded(sIV,eIV,selectedId);
				  
				});
  });
  
  
  //Search Time interval

function changeList(selectValue){

  [start_el, end_el].forEach(input => {
    input.addEventListener("change", () => {
	const start_el = document.getElementById("start");
	const end_el = document.getElementById("end");
  	const start = start_el.value;
  	const end= end_el.value;

	console.log(start);
	console.log(end);
      // 如果兩者都有值再發送請求
      if (start && end) {
  		
  		distTicketList_loaded(start,end,selectValue)
  		}
  		})
  		});
  		}
		
		
  let dataTableInstance = null;
  function distTicketList_loaded(startTime,endTime,selectValue) {
	
	/*if (dataTableInstance) {
     	   dataTableInstance.clear().destroy();
											        }*/
  	tbody_el.innerHTML = "";

  	fetch('/maven-tickeasy-v1/eventdetail/dist-ticket-list', {
  		method: `POST`,
  		headers: { 'Content-Type': 'application/json' },
  		body: JSON.stringify({
			startTime: startTime, endTime: endTime, selectValue: selectValue
  		})
  	})
  		.then(resp => resp.json())
  		.then(distTicketLists => {
  			if(!Array.isArray(distTicketLists)){
  									distTicketLists = [];
  						}
				/*for (let distTicketList of distTicketLists) {

				

											tbody_el.insertAdjacentHTML("beforeend", `
												<tr>
												                 <td>${distTicketList.distId}</td>
																 <td>${distTicketList.ticketId}</td>
												                 <td>${distTicketList.buyerOrder.memberId}</td>
												                 <td>${distTicketList.receivedMemberId}</td>
												                 <td>${distTicketList.distedTime}</td>
												                 <td>已分票</td>
												               </tr>
								                `)
											
										}*/
										
										if (dataTableInstance) {
											dataTableInstance.clear();        
											dataTableInstance.rows.add(distTicketLists.map(item => [
											                item.distId,  // 第一列
											                item.ticketId, // 第二列
											                item.buyerOrder.memberId, // 第三列
											                item.receivedMemberId, // 第四列
											                item.distedTime,  // 第五列
											                '已分票'  // 第六列
											            ]));  
										           dataTableInstance.draw();  // 重新繪製表格
										       } else {
										dataTableInstance=new DataTable('#example', {
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
											dataTableInstance.rows.add(distTicketLists.map(item => [
											                item.distId,
											                item.ticketId,
											                item.buyerOrder.memberId,
											                item.receivedMemberId,
											                item.distedTime,
											                '已分票'
											            ])).draw();
				
						}
						})
											
				}
				//jquery Datatable的因素
						/*						$('#datatable_search').on('input', function () {
												    var searchValue = this.value;  // 獲取輸入框的值
												    
												    dataTableInstance.columns().every(function () {
												        var column = this;
												        
												        // 排除時間欄位的篩選，假設時間在第五列 (index 4)
												        if (column.index() !== 4) {
												            column.search(searchValue).draw();  // 使用 search() 來設置篩選條件
												        }
												    });
													dataTableInstance.draw(); 
												});

	*/
    /*fetch("/maven-tickeasy-v1/eventdetail/dist-ticket-list", {
        method: `POST`,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ startTime: start, endTime: end })
      })
        .then(resp => resp.json())
        .then(data => {
         
          tbody_el.innerHTML = ""; // 清空表格
          
          data.forEach(row => {
            tbody_el.insertAdjacentHTML("beforeend", `
				<tr>
	                 <td>${row.distId}</td>
					 <td>${row.ticketId}</td>
	                 <td>${row.buyerOrder.memberId}</td>
	                 <td>${row.receivedMemberId}</td>
	                 <td>${row.updateTime}</td>
	                 <td>已分票</td>
	               </tr>
            `);
          });
        });
    }
  });
});*/
