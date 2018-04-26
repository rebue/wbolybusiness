(function($) {
	// 当domReady的时候开始初始化
	$(function() {
		var $wrap = $('#uploader'),
			// 图片容器
			$queue = $('<ul class="filelist"></ul>').appendTo($wrap.find('.queueList')),
			// 状态栏，包括进度和控制按钮
			$statusBar = $wrap.find('.statusBar'),
			// 文件总体选择信息。
			$info = $statusBar.find('.info'),
			// 上传按钮
			$upload = $wrap.find('.uploadBtn'),
			// 没选择文件之前的内容。
			$placeHolder = $wrap.find('.placeholder'),
			$progress = $statusBar.find('.progress').hide(),
			// 添加的文件数量
			fileCount = 0,
			// 添加的文件总大小
			fileSize = 0,
			// 优化retina, 在retina下这个值是2
			ratio = window.devicePixelRatio || 1,
			// 缩略图大小
			thumbnailWidth = 110 * ratio,
			thumbnailHeight = 110 * ratio,
			// 可能有pedding, ready, uploading, confirm, done.
			state = 'pedding',
			// 所有文件的进度信息，key为file id
			percentages = {},
			// 判断浏览器是否支持图片的base64
			isSupportBase64 = (function() {
				var data = new Image();
				var support = true;
				data.onload = data.onerror = function() {
					if(this.width != 1 || this.height != 1) {
						support = false;
					}
				}
				data.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
				return support;
			})(),

			// 实例化
			uploader = WebUploader
			.create({
				pick: {
					id: '#filePicker',
					label: label
				},
				formData: {
					flag: "wechat"
				},
				chunked: false, // 开起分片上传
				chunkSize: 512 * 1024, // 默认大小为5M     
				//server: '/wbolyweb/web/order/upload.htm',
				server: serverUrl,
				accept: {
					title: 'Images',
					extensions: 'jpg,jpeg,png',
					mimeTypes: 'image/jpg,image/jpeg,image/png' //修改这行
				},
				runtimeOrder:'html5',
				fileNumLimit: NumLimit,
				fileSizeLimit: SizeLimit, // 15 M     
				fileSingleSizeLimit: SingleSizeLimit // 3 M         
			});

		// 拖拽时不接受 js, txt 文件。
		uploader.on('dndAccept', function(items) {
			var denied = false,
				len = items.length,
				i = 0,
				// 修改js类型
				unAllowed = 'text/plain;application/javascript ';

			for(; i < len; i++) {
				// 如果在列表里面
				if(~unAllowed.indexOf(items[i].type)) {
					denied = true;
					break;
				}
			}

			return !denied;
		});

		// 添加“添加文件”的按钮，
		uploader.addButton({
			id: '#filePicker2',
			label: '继续添加'
		});

		uploader.on('ready', function() {
			window.uploader = uploader;
		});

		uploader.on('uploadFinished', function(file, response) {	
			if(uploader.getStats().uploadFailNum==0){ //上传失败的文件数=0时，才执行提交
				ajaxupdate();
			}
		});
		
		uploader.on('uploadSuccess', function(file, response) {	
			arr.push(response.path);
		});
		
		// 当有文件添加进来时执行，负责view的创建
		function addFile(file) {
			var $li = $('<li id="' + file.id + '">' + '<p class="title">' + file.name + '</p>' +
					'<p class="imgWrap"></p>' + '<p class="progress"><span></span></p>' + '</li>'),

				$btns = $(
					'<div class="file-panel"><span class="cancel">删除</span></div>')
				.appendTo($li),
				$prgress = $li.find('p.progress span'),
				$wrap = $li.find('p.imgWrap'),
				$info = $('<p class="error"></p>'),

				showError = function(code) {
					switch(code) {
						case 'exceed_size':
							text = '文件大小超出';
							break;

						case 'interrupt':
							text = '上传暂停';
							break;

						default:
							text = '上传失败';
							break;
					}

					$info.text(text).appendTo($li);
				};

			if(file.getStatus() === 'invalid') {
				showError(file.statusText);
			} else {
				// @todo lazyload
				$wrap.text('预览中');
				uploader.makeThumb(file, function(error, src) {
					var img;

					if(error) {
						$wrap.text('不能预览');
						return;
					}

					if(isSupportBase64) {
						img = $('<img src="' + src + '">');
						$wrap.empty().append(img);
					} else {
						$.ajax('../../resources/server/preview.php', {
							method: 'POST',
							data: src,
							dataType: 'json'
						}).done(function(response) {
							if(response.result) {
								img = $('<img src="' + response.result + '">');
								$wrap.empty().append(img);
							} else {
								$wrap.text("预览出错");
							}
						});
					}
				}, thumbnailWidth, thumbnailHeight);

				percentages[file.id] = [file.size, 0];
				file.rotation = 0;
			}

			file.on('statuschange', function(cur, prev) {
				if(prev === 'progress') {
					$prgress.hide().width(0);
				} else if(prev === 'queued') {
					$li.off('mouseenter mouseleave');
					$btns.remove();
				}

				// 成功
				if(cur === 'error' || cur === 'invalid') {
					console.log(file.statusText);
					showError(file.statusText);
					percentages[file.id][1] = 1;
				} else if(cur === 'interrupt') {
					showError('interrupt');
				} else if(cur === 'queued') {
					percentages[file.id][1] = 0;
				} else if(cur === 'progress') {
					$info.remove();
					$prgress.css('display', 'block');
				} else if(cur === 'complete') {
					$li.append('<span class="success"></span>');
				}

				$li.removeClass('state-' + prev).addClass('state-' + cur);
			});

			$li.on('mouseenter', function() {
				$btns.stop().animate({
					height: 30
				});
			});

			$li.on('mouseleave', function() {
				$btns.stop().animate({
					height: 0
				});
			});

			$btns.on('click', 'span', function() {
				var index = $(this).index(),
					deg;

				switch(index) {
					case 0:
						uploader.removeFile(file);
						return;
				}
			});

			$li.appendTo($queue);
		}

		// 负责view的销毁
		function removeFile(file) {
			var $li = $('#' + file.id);

			delete percentages[file.id];
			updateTotalProgress();
			$li.off().find('.file-panel').off().end().remove();
		}

		function updateTotalProgress() {
			var loaded = 0,
				total = 0,
				spans = $progress.children(),
				percent;

			$.each(percentages, function(k, v) {
				total += v[0];
				loaded += v[0] * v[1];
			});

			percent = total ? loaded / total : 0;

			spans.eq(0).text(Math.round(percent * 100) + '%');
			spans.eq(1).css('width', Math.round(percent * 100) + '%');
			updateStatus();
		}

		function updateStatus() {
			var text = '',
				stats;

			if(state === 'ready') {
				text = '选中' + fileCount + '张图片，共' + WebUploader.formatSize(fileSize) + '。';
			} else if(state === 'confirm') {
				stats = uploader.getStats();
				if(stats.uploadFailNum) {
					text = '已成功上传' + stats.successNum + '张照片，' + stats.uploadFailNum +
						'张上传失败，<a class="retry" href="#">重新上传</a>失败图片'
				}

			} else {
				stats = uploader.getStats();
				text = '共' + fileCount + '张（' + WebUploader.formatSize(fileSize) + '），已上传' + stats.successNum + '张';

				if(stats.uploadFailNum) {
					text += '，失败' + stats.uploadFailNum + '张';
				}
			}

			$info.html(text);
		}

		function setState(val) {
			var file, stats;

			if(val === state) {
				return;
			}

			$upload.removeClass('state-' + state);
			$upload.addClass('state-' + val);
			state = val;

			switch(state) {
				case 'pedding':
					$placeHolder.removeClass('element-invisible');
					$queue.hide();
					$statusBar.addClass('element-invisible');
					uploader.refresh();
					break;

				case 'ready':
					$placeHolder.addClass('element-invisible');
					$('#filePicker2').removeClass('element-invisible');
					$queue.show();
					$statusBar.removeClass('element-invisible');
					uploader.refresh();
					break;

				case 'uploading':
					$('#filePicker2').addClass('element-invisible');
					$progress.show();
					$upload.text('暂停上传');
					break;

				case 'paused':
					$progress.show();
					$upload.text('继续上传');
					break;

				case 'confirm':
					$progress.hide();
					$('#filePicker2').removeClass('element-invisible');
					$upload.text('开始上传');

					stats = uploader.getStats();
					if(stats.successNum && !stats.uploadFailNum) {
						setState('finish');
						return;
					}
					break;
				case 'finish':
					stats = uploader.getStats();
					if(stats.successNum) {
						/*alert( '上传成功' );*/
					} else {
						// 没有成功的图片，重设
						state = 'done';
						location.reload();
					}
					break;
			}
			updateStatus();
		}

		uploader.onUploadProgress = function(file, percentage) {
			var $li = $('#' + file.id),
				$percent = $li.find('.progress span');

			$percent.css('width', percentage * 100 + '%');
			percentages[file.id][1] = percentage;
			updateTotalProgress();
		};

		uploader.onFileQueued = function(file) {
			fileCount++;
			fileSize += file.size;

			if(fileCount === 1) {
				$placeHolder.addClass('element-invisible');
				$statusBar.show();
			}

			addFile(file);
			setState('ready');
			updateTotalProgress();
		};

		uploader.onFileDequeued = function(file) {
			fileCount--;
			fileSize -= file.size;

			if(!fileCount) {
				setState('pedding');
			}

			removeFile(file);
			updateTotalProgress();

		};

		uploader.on('all', function(type) {
			var stats;
			switch(type) {
				case 'uploadFinished':
					setState('confirm');
					break;

				case 'startUpload':
					setState('uploading');
					break;

				case 'stopUpload':
					setState('paused');
					break;

			}
		});
		//错误提示方法一
		uploader.onError = function(code) {
			if(code == "Q_EXCEED_NUM_LIMIT") {
				mui.alert('超出最大上传数量');
			}
			if(code == "Q_EXCEED_SIZE_LIMIT") {
				mui.alert('文件总大小过大');
			}
			if(code == "F_DUPLICATE") {
				mui.alert('图片重复')
			}
			if(code == "F_EXCEED_SIZE") {
				mui.alert('图片超大小范围')
			}
			if(code == "Runtime Error") {
				mui.alert('上传超时')
			}
			//alert( 'Eroor: ' + code );
		};

		$upload.on('click', function() {
			if($(this).hasClass('disabled')) {
				return false;
			}

			if(state === 'ready') {
				uploader.upload();
			} else if(state === 'paused') {
				uploader.upload();
			} else if(state === 'uploading') {
				uploader.stop();
			}
		});

		$info.on('click', '.retry', function() {
			uploader.retry();
		});

		$upload.addClass('state-' + state);
		updateTotalProgress();
	});

})(jQuery);