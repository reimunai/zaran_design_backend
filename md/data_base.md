# 扎染图案生成与协同设计系统 - 数据库数据字典

> 基于 MySQL 8.0 设计，覆盖用户管理、草图绘制、AI生成、图案管理与协同、工艺知识库、数字藏品、系统监控等全部模块。

---

## 1. 用户管理模块

### 1.1 用户表 (users)

存储所有用户的基本信息，角色区分通过 `role` 字段实现，设计师/传承人的扩展信息采用可空字段。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| user_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 用户唯一编号 |
| username | VARCHAR | 50 | NOT NULL | UK | - | 用户名，登录账号 |
| password | VARCHAR | 255 | NOT NULL | - | - | 加密密码（BCrypt） |
| phone | VARCHAR | 20 | NULL | UK | NULL | 手机号 |
| email | VARCHAR | 100 | NULL | UK | NULL | 邮箱 |
| role | ENUM | 'admin','inheritor','designer','tourist' | NOT NULL | - | 'tourist' | 角色：管理员/传承人/设计师/游客 |
| avatar | VARCHAR | 255 | NULL | - | NULL | 头像存储路径 |
| bio | TEXT | - | NULL | - | NULL | 个人简介 |
| professional_field | VARCHAR | 100 | NULL | - | NULL | 专业领域（设计师） |
| years_of_experience | INT | - | NULL | - | NULL | 从业年限（设计师/传承人） |
| portfolio_url | VARCHAR | 255 | NULL | - | NULL | 个人作品集链接（设计师） |
| inheritance_project | VARCHAR | 100 | NULL | - | NULL | 传承项目（传承人） |
| mentorship | VARCHAR | 255 | NULL | - | NULL | 师承关系（传承人） |
| certificate | VARCHAR | 255 | NULL | - | NULL | 荣誉证书存储路径 |
| certification_status | ENUM | 'pending','approved','rejected' | NOT NULL | - | 'pending' | 设计师/传承人认证状态 |
| certified_by | INT | - | NULL | FK → users(user_id) | NULL | 审核人（传承人审核设计师） |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 注册时间 |
| updated_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_disabled | BOOLEAN | - | NOT NULL | - | FALSE | 是否被禁用（管理员操作） |

### 1.2 用户认证申请表 (user_certification_requests)

当游客申请成为设计师或设计师申请成为传承人时提交，由传承人或管理员审核。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| request_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 申请编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 申请人ID |
| target_role | ENUM | 'designer','inheritor' | NOT NULL | - | - | 申请成为的角色 |
| reason | TEXT | - | NULL | - | NULL | 申请理由 |
| attachments | VARCHAR | 255 | NULL | - | NULL | 证明材料路径 |
| status | ENUM | 'pending','approved','rejected' | NOT NULL | - | 'pending' | 审核状态 |
| reviewer_id | INT | - | NULL | FK → users(user_id) | NULL | 审核人（传承人或管理员） |
| review_comment | VARCHAR | 255 | NULL | - | NULL | 审核意见 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 申请时间 |
| reviewed_at | DATETIME | - | NULL | - | NULL | 审核时间 |

---

## 2. 草图绘制模块

### 2.1 草图信息表 (sketches)

存储用户绘制的草图元数据，矢量数据以JSON格式存储（如图层、路径等）。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| sketch_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 草图编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 所属用户 |
| name | VARCHAR | 100 | NOT NULL | - | '未命名草图' | 草图名称 |
| width | INT | - | NOT NULL | - | 1024 | 画布宽度（像素） |
| height | INT | - | NOT NULL | - | 1024 | 画布高度（像素） |
| layers_json | JSON | - | NULL | - | NULL | 图层数据（矢量路径、样式等） |
| thumbnail_path | VARCHAR | 255 | NULL | - | NULL | 缩略图存储路径（PNG） |
| is_public | BOOLEAN | - | NOT NULL | - | FALSE | 是否公开 |
| category_id | INT | - | NULL | FK → sketch_categories(category_id) | NULL | 所属分类（云纹/螺旋纹等） |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted_at | DATETIME | - | NULL | - | NULL | 软删除时间 |

### 2.2 草图版本记录表 (sketch_versions)

支持版本回退，每次保存生成一个版本。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| version_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 版本编号 |
| sketch_id | INT | - | NOT NULL | FK → sketches(sketch_id) | - | 所属草图 |
| version_number | INT | - | NOT NULL | UK(sketch_id,version_number) | - | 版本号（从1递增） |
| layers_json | JSON | - | NOT NULL | - | - | 该版本的完整图层数据 |
| thumbnail_path | VARCHAR | 255 | NULL | - | NULL | 版本缩略图 |
| change_description | VARCHAR | 255 | NULL | - | NULL | 修改描述 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 保存时间 |

### 2.3 草图分类表 (sketch_categories)

支持多级分类，如云纹、螺旋纹、鱼鳞纹等。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| category_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 分类编号 |
| parent_id | INT | - | NULL | FK → sketch_categories(category_id) | NULL | 父分类ID（NULL表示一级） |
| name | VARCHAR | 50 | NOT NULL | UK(parent_id, name) | - | 分类名称（如“云纹”） |
| description | VARCHAR | 255 | NULL | - | NULL | 分类描述 |
| sort_order | INT | - | NOT NULL | - | 0 | 显示顺序 |

---

## 3. AI生成模块

### 3.1 生成任务表 (generation_tasks)

记录每次提交的生成任务。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| task_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 任务编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 提交用户 |
| sketch_id | INT | - | NOT NULL | FK → sketches(sketch_id) | - | 基于的草图 |
| k_value | TINYINT | 2,3,4 | NOT NULL | - | 3 | 量化级别（二道/三道/四道浸染） |
| noise_level | DECIMAL | 3,2 | NOT NULL | - | 0.5 | 噪声强度 [0.0,1.0] |
| patch_mode | TINYINT | 1,2,4,8 | NOT NULL | - | 4 | 边缘分块数 |
| style_reference_id | INT | - | NULL | FK → patterns(pattern_id) | NULL | 风格参考图案ID（可选） |
| status | ENUM | 'queued','processing','completed','failed' | NOT NULL | - | 'queued' | 任务状态 |
| priority | TINYINT | 1-10 | NOT NULL | - | 5 | 优先级（数值越大越高） |
| is_batch | BOOLEAN | - | NOT NULL | - | FALSE | 是否属于批量任务 |
| batch_id | VARCHAR | 36 | NULL | - | NULL | 批量任务组ID（UUID） |
| started_at | DATETIME | - | NULL | - | NULL | 开始处理时间 |
| completed_at | DATETIME | - | NULL | - | NULL | 完成时间 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 提交时间 |

### 3.2 生成结果表 (generation_results)

每个任务生成一个结果图片。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| result_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 结果编号 |
| task_id | INT | - | NOT NULL | UNIQUE | - | 关联的任务ID（1:1） |
| image_path | VARCHAR | 255 | NOT NULL | - | - | 生成图片存储路径（PNG） |
| thumbnail_path | VARCHAR | 255 | NULL | - | NULL | 缩略图路径 |
| user_rating | TINYINT | 1-5 | NULL | - | NULL | 用户评分（1-5） |
| is_favorite | BOOLEAN | - | NOT NULL | - | FALSE | 是否被用户收藏 |
| feedback_comment | VARCHAR | 255 | NULL | - | NULL | 评价备注 |
| download_count | INT | - | NOT NULL | - | 0 | 下载次数 |
| share_count | INT | - | NOT NULL | - | 0 | 分享次数 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 生成完成时间 |

### 3.3 批量任务子项关联表 (batch_task_items)

用于批量生成时记录子任务与主任务的关系。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| item_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 子项ID |
| batch_id | VARCHAR | 36 | NOT NULL | - | - | 批量任务ID |
| task_id | INT | - | NOT NULL | UNIQUE | - | 子任务ID |
| param_combination | JSON | - | NOT NULL | - | - | 该子任务的参数组合 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | - |

---

## 4. 图案管理与协同模块

### 4.1 图案信息表 (patterns)

用户公开发布或保存的作品，可能来源于生成结果或手动编辑。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| pattern_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 图案编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 发布者/所有者 |
| name | VARCHAR | 100 | NOT NULL | - | '未命名作品' | 图案名称 |
| image_path | VARCHAR | 255 | NOT NULL | - | - | 作品图片路径 |
| thumbnail_path | VARCHAR | 255 | NOT NULL | - | - | 缩略图路径 |
| source_type | ENUM | 'generated','manual','collaborative' | NOT NULL | - | 'generated' | 来源类型 |
| source_id | INT | - | NULL | - | NULL | 来源ID（generation_results.result_id或sketch_id） |
| avg_rating | DECIMAL | 3,2 | NULL | - | NULL | 平均评分（由评论计算） |
| view_count | INT | - | NOT NULL | - | 0 | 浏览次数 |
| like_count | INT | - | NOT NULL | - | 0 | 点赞数 |
| collect_count | INT | - | NOT NULL | - | 0 | 收藏数 |
| comment_count | INT | - | NOT NULL | - | 0 | 评论数 |
| is_public | BOOLEAN | - | NOT NULL | - | FALSE | 是否公开 |
| tags | VARCHAR | 255 | NULL | - | NULL | 逗号分隔的标签字符串（冗余，便于搜索） |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted_at | DATETIME | - | NULL | - | NULL | 软删除时间 |

### 4.2 图案标签表 (pattern_tags)

规范化标签管理，支持多对多。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| tag_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 标签编号 |
| tag_name | VARCHAR | 50 | NOT NULL | UNIQUE | - | 标签名称（如“云纹”） |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |

**图案-标签关联表 (pattern_tag_relation)**

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 图案ID |
| tag_id | INT | - | NOT NULL | FK → pattern_tags(tag_id) | - | 标签ID |
| PRIMARY KEY (pattern_id, tag_id) | - | - | - | - | - | 复合主键 |

### 4.3 图案收藏表 (pattern_favorites)

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| favorite_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 收藏编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 收藏者 |
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 被收藏的图案 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 收藏时间 |
| UNIQUE KEY (user_id, pattern_id) | - | - | - | - | - | 防止重复收藏 |

### 4.4 图案评论表 (pattern_comments)

支持楼中楼，通过 parent_id 实现。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| comment_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 评论编号 |
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 所属图案 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 评论用户 |
| parent_id | INT | - | NULL | FK → pattern_comments(comment_id) | NULL | 父评论ID（NULL为顶层） |
| content | TEXT | - | NOT NULL | - | - | 评论内容 |
| like_count | INT | - | NOT NULL | - | 0 | 点赞数 |
| is_cultural_review | BOOLEAN | - | NOT NULL | - | FALSE | 是否为传承人“文化点评” |
| status | ENUM | 'visible','hidden','deleted' | NOT NULL | - | 'visible' | 状态 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 评论时间 |
| updated_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 4.5 协同编辑会话表 (collab_sessions)

记录协同项目的会话信息。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| session_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 会话编号 |
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 协同的作品ID |
| owner_id | INT | - | NOT NULL | FK → users(user_id) | - | 创建者（主设计者） |
| session_token | VARCHAR | 64 | NOT NULL | UNIQUE | - | WebSocket会话唯一标识 |
| status | ENUM | 'active','archived','closed' | NOT NULL | - | 'active' | 会话状态 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| closed_at | DATETIME | - | NULL | - | NULL | 关闭时间 |

### 4.6 协同参与人表 (collab_participants)

会话中的协作者及其权限。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| participant_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 记录ID |
| session_id | INT | - | NOT NULL | FK → collab_sessions(session_id) | - | 会话ID |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 协作者ID |
| permission | ENUM | 'view','comment','edit' | NOT NULL | - | 'view' | 权限 |
| joined_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 加入时间 |
| UNIQUE KEY (session_id, user_id) | - | - | - | - | - | - |

### 4.7 协同编辑操作记录表 (collab_operations)

存储协同过程中的每次编辑操作，用于版本历史和回滚。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| op_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 操作编号 |
| session_id | INT | - | NOT NULL | FK → collab_sessions(session_id) | - | 会话ID |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 执行操作的用户 |
| op_type | VARCHAR | 20 | NOT NULL | - | - | 操作类型（draw/delete/move/layer等） |
| op_data | JSON | - | NOT NULL | - | - | 操作数据（路径、坐标等） |
| version_number | INT | - | NOT NULL | - | - | 版本号（递增） |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 操作时间 |
| INDEX (session_id, version_number) | - | - | - | - | - | 用于回滚 |

---

## 5. 工艺知识库模块

### 5.1 工艺知识点表 (knowledge_entries)

存储扎染相关的图文知识。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| entry_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 知识编号 |
| title | VARCHAR | 200 | NOT NULL | - | - | 标题 |
| content | LONGTEXT | - | NOT NULL | - | - | 正文（支持富文本） |
| category | ENUM | 'tie_dye','dyeing','pattern','material','history','term' | NOT NULL | - | - | 分类 |
| author_id | INT | - | NOT NULL | FK → users(user_id) | - | 创建者（传承人或管理员） |
| reviewer_id | INT | - | NULL | FK → users(user_id) | NULL | 审核人（管理员） |
| status | ENUM | 'pending','published','rejected' | NOT NULL | - | 'pending' | 发布状态 |
| cover_image | VARCHAR | 255 | NULL | - | NULL | 封面图片路径 |
| view_count | INT | - | NOT NULL | - | 0 | 浏览次数 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| published_at | DATETIME | - | NULL | - | NULL | 发布时间 |

### 5.2 工艺与图案关联表 (knowledge_pattern_links)

多对多关联知识条目与图案。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| link_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 关联编号 |
| entry_id | INT | - | NOT NULL | FK → knowledge_entries(entry_id) | - | 知识ID |
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 图案ID |
| relation_type | ENUM | 'inspiration','technique','example' | NOT NULL | - | 'example' | 关联类型 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| UNIQUE KEY (entry_id, pattern_id, relation_type) | - | - | - | - | - | - |

### 5.3 术语词典表 (term_dictionary)

独立术语表，供知识库引用。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| term_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 术语编号 |
| term_name | VARCHAR | 100 | NOT NULL | UK | - | 术语名称 |
| pinyin | VARCHAR | 100 | NULL | - | NULL | 拼音 |
| english | VARCHAR | 200 | NULL | - | NULL | 英文翻译 |
| definition | TEXT | - | NOT NULL | - | - | 定义 |
| example_image | VARCHAR | 255 | NULL | - | NULL | 示例图片路径 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |

### 5.4 用户知识收藏与笔记表 (knowledge_notes)

用户可收藏知识条目并添加个人笔记。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| note_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 笔记编号 |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 用户ID |
| entry_id | INT | - | NOT NULL | FK → knowledge_entries(entry_id) | - | 知识条目ID |
| personal_note | TEXT | - | NULL | - | NULL | 个人笔记 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 收藏时间 |
| updated_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP ON UPDATE | 笔记更新时间 |
| UNIQUE KEY (user_id, entry_id) | - | - | - | - | - | 防止重复收藏 |

---

## 6. 数字藏品与版权管理模块

### 6.1 数字藏品记录表 (digital_collections)

记录铸造成数字藏品的图案。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| collection_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 藏品编号 |
| pattern_id | INT | - | NOT NULL | FK → patterns(pattern_id) | - | 图案ID |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 持有者用户ID |
| blockchain_hash | VARCHAR | 128 | NULL | UK | NULL | 区块链交易哈希（可选） |
| token_id | VARCHAR | 100 | NULL | - | NULL | 数字凭证ID |
| minted_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 铸造时间 |
| status | ENUM | 'active','transferred','burned' | NOT NULL | - | 'active' | 状态 |

### 6.2 授权记录表 (authorization_records)

版权授权管理。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| auth_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 授权编号 |
| collection_id | INT | - | NOT NULL | FK → digital_collections(collection_id) | - | 藏品ID |
| licensor_id | INT | - | NOT NULL | FK → users(user_id) | - | 授权人 |
| licensee_id | INT | - | NOT NULL | FK → users(user_id) | - | 被授权人 |
| auth_type | ENUM | 'commercial','non_commercial','academic' | NOT NULL | - | - | 授权类型 |
| start_date | DATE | - | NOT NULL | - | - | 授权开始日期 |
| end_date | DATE | - | NULL | - | NULL | 授权截止日期（NULL为永久） |
| fee | DECIMAL | 10,2 | NULL | - | NULL | 授权费用 |
| status | ENUM | 'active','expired','revoked' | NOT NULL | - | 'active' | 状态 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 7. 系统管理与监控模块

### 7.1 操作日志表 (system_logs)

记录敏感操作及用户行为。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| log_id | BIGINT | - | NOT NULL | PK | AUTO_INCREMENT | 日志编号 |
| user_id | INT | - | NULL | FK → users(user_id) | NULL | 操作用户ID（NULL表示系统） |
| operation | VARCHAR | 50 | NOT NULL | - | - | 操作类型（DELETE_PATTERN, UPDATE_ROLE等） |
| target_type | VARCHAR | 50 | NULL | - | NULL | 目标类型（pattern, user, comment） |
| target_id | INT | - | NULL | - | NULL | 目标ID |
| content | TEXT | - | NULL | - | NULL | 操作详情（JSON） |
| ip_address | VARCHAR | 45 | NULL | - | NULL | 客户端IP |
| user_agent | VARCHAR | 255 | NULL | - | NULL | 浏览器UA |
| result | ENUM | 'success','failure' | NOT NULL | - | 'success' | 执行结果 |
| error_msg | VARCHAR | 255 | NULL | - | NULL | 错误信息 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 操作时间 |
| INDEX (created_at), INDEX (user_id) | - | - | - | - | - | - |

### 7.2 API调用统计表 (api_stats)

用于监控API性能。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| stat_id | BIGINT | - | NOT NULL | PK | AUTO_INCREMENT | 统计ID |
| api_path | VARCHAR | 200 | NOT NULL | - | - | API路径 |
| method | VARCHAR | 10 | NOT NULL | - | - | 请求方法（GET,POST等） |
| call_count | INT | - | NOT NULL | - | 0 | 调用次数 |
| total_time | BIGINT | - | NOT NULL | - | 0 | 总耗时（毫秒） |
| max_time | INT | - | NOT NULL | - | 0 | 最大响应时间（毫秒） |
| error_count | INT | - | NOT NULL | - | 0 | 错误次数（HTTP 5xx） |
| stat_date | DATE | - | NOT NULL | - | - | 统计日期 |
| UNIQUE KEY (api_path, method, stat_date) | - | - | - | - | - | - |

### 7.3 任务队列监控表 (queue_monitor)

记录AI生成队列的状态（可选，用于监控）。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| monitor_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 监控ID |
| queue_length | INT | - | NOT NULL | - | - | 当前队列长度 |
| avg_wait_time | DECIMAL | 10,2 | NULL | - | NULL | 平均等待时间（秒） |
| failed_count_24h | INT | - | NOT NULL | - | 0 | 24小时内失败任务数 |
| recorded_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 记录时间 |
| INDEX (recorded_at) | - | - | - | - | - | 用于趋势分析 |

---

## 8. 消息通知表 (notification)

用于通知用户批量生成完成、协同邀请、审核结果等。

| 字段名 | 数据类型 | 长度/值 | 允许空 | 键/约束 | 默认值 | 说明 |
|--------|----------|---------|--------|---------|--------|------|
| notify_id | INT | - | NOT NULL | PK | AUTO_INCREMENT | 通知ID |
| user_id | INT | - | NOT NULL | FK → users(user_id) | - | 接收用户 |
| type | VARCHAR | 30 | NOT NULL | - | - | 类型（batch_complete, collab_invite, audit_result） |
| title | VARCHAR | 100 | NOT NULL | - | - | 通知标题 |
| content | VARCHAR | 255 | NOT NULL | - | - | 通知内容 |
| link | VARCHAR | 255 | NULL | - | NULL | 跳转链接 |
| is_read | BOOLEAN | - | NOT NULL | - | FALSE | 是否已读 |
| created_at | DATETIME | - | NOT NULL | - | CURRENT_TIMESTAMP | 创建时间 |
| INDEX (user_id, is_read) | - | - | - | - | - | - |

---

## 索引与性能优化摘要

根据业务需求及性能预估，关键索引设计如下：

| 表名 | 索引字段 | 索引类型 | 用途 |
|------|----------|----------|------|
| generation_tasks | (user_id, created_at) | 复合索引 | 查询用户生成历史 |
| generation_results | (task_id) | 唯一索引 | 任务与结果1:1关联 |
| patterns | (is_public, created_at) | 复合索引 | 作品广场排序查询 |
| pattern_favorites | (user_id, pattern_id) | 唯一索引 | 防止重复收藏 |
| pattern_comments | (pattern_id, created_at) | 复合索引 | 拉取评论列表 |
| collab_operations | (session_id, version_number) | 复合索引 | 版本回滚 |
| system_logs | (created_at) | 普通索引 | 日志清理与时间范围查询 |
| system_logs | (user_id) | 普通索引 | 按用户检索日志 |
| notification | (user_id, is_read) | 复合索引 | 拉取未读通知 |
| sketch_versions | (sketch_id, version_number) | 唯一约束 | 版本号唯一 |

---

*本数据字典基于 MySQL 8.0 语法编写，满足第三范式（3NF），可直接用于生成建表脚本。*