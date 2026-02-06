import { Col ,Row} from "antd"; 
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";

export default function EditDeleteButtons({ post, user, onEdit, dispatch, deletePostRequest }) {
  // 작성자 본인인지 ( 닉네임 비교)  
  const isAuthor =  user?.nickname?.trim().toLowerCase() ===  post.authorNickname?.trim().toLowerCase();

  // 작성자 아니면 버튼표시 안함.
  if (!isAuthor) return null;

  return (
    <Row>
      {/* 수정 */}
      <Col flex={1} style={{ textAlign: "center"  , marginRight: "20px"}}>  
        <div
          onClick={() => onEdit(post)}               
          style={{ cursor: "pointer" }}              
        >
          <EditOutlined style={{ fontSize: "20px", color: "#555" }} /> 
          <div style={{ fontSize: "12px" }}>수정</div>  
        </div>
      </Col>
      {/* 삭제 */}
      <Col flex={1} style={{ textAlign: "center" }}>  
        <div
          onClick={() => dispatch(deletePostRequest({ postId: post.id }))}  
          style={{ cursor: "pointer" }}                                 
        >
          <DeleteOutlined style={{ fontSize: "20px", color: "red" }} />
          <div style={{ fontSize: "12px" }}>삭제</div>  
        </div>
      </Col>
    </Row>
  );
}
