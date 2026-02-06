import { Modal, Form, Input, Upload, Button, Select } from "antd";
 
export default function EditPostModal({
  visible,
  editPost,
  onCancel,
  onSubmit,
  uploadFiles,
  setUploadFiles,
}) {
  return (
    <Modal title="글 수정" open={visible} onCancel={onCancel} footer={null}>
      <Form
        initialValues={{
          content: editPost?.content,
          hashtags: Array.isArray(editPost?.hashtags)
            ? editPost.hashtags
            : editPost?.hashtags
            ? editPost.hashtags.split(",")
            : [],
        }}
        onFinish={onSubmit}
        layout="vertical"
      >
        <Form.Item name="content" label="내용">
          <Input.TextArea rows={4} />
        </Form.Item>

        <Form.Item name="hashtags" label="해시태그">
          <Select mode="tags" style={{ width: "100%" }} placeholder="해시태그 입력" />
        </Form.Item>

        <Form.Item label="이미지 업로드">
          <Upload
            multiple
            beforeUpload={() => false}
            onChange={({ fileList }) =>
              setUploadFiles(fileList.map((f) => f.originFileObj))
            }
          >
            <Button>이미지 선택</Button>
          </Upload>
        </Form.Item>

        <Button type="primary" htmlType="submit">
          수정 완료
        </Button>
      </Form>
    </Modal>
  );
}


// export default function EditPostModal(){
//     return <h1>EditPostModal</h1>;
// }