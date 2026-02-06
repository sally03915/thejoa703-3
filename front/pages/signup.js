// 1. import 어떤역할 주석
import React, { useState, useEffect } from "react"; // view상태관리 (이벤트, 변수)
import { useRouter } from "next/router"; // 경로
import { useDispatch, useSelector } from "react-redux";  // 리듀서상태관리 (useSelector: reducer가져오기 ,useDispatch )
import { Row, Col, Form, Input, Button, Upload, Spin, message } from "antd";   // 컴포넌트  
import { UploadOutlined } from "@ant-design/icons";  // 아이콘
import { signupRequest , resetAuthState} from "../reducers/authReducer";  // 액션 - 회원가입, 리셋
import axios from "../api/axios";   // boot +  accessToken + refresh Token 

export default function SignupPage(){
    //code
    //2. 부품사용가능하게 router, dispatch 
    const dispatch  = useDispatch();  // 이벤트 발생시 store 알림
    const router    = useRouter();    // 이동- 경로바꾸기
    //3. useDispatch 이용해서 reducer의 상태가져오기, loading, error, success
    const { loading, error, success}    = useSelector((state)=>state.auth);  
 
    const [fileList, setFileList] = useState([]);

    const onFinish = (values)=>{
        const formData = new FormData();
        formData.append("email" , values.email);
        formData.append("password" , values.password);
        formData.append("nickname" , values.nickname);
        formData.append("provider" , "local");
        if( fileList.length > 0){
            formData.append("ufile" , fileList[0].originFileObj);
        }
        dispatch( signupRequest(formData) );  //signupSuccess, signupFailure
    }

    //회원가입 성공시 메시지 + 로그인 페이지로 이동
    useEffect(  ()=>{
        if(success){
            message.success("회원가입이 성공적으로 완료되었습니다.");
            router.push("/login");
            dispatch( resetAuthState() );
        }
    } , [success,router, dispatch]); 

    /////////////////////////////////
    //view
    return (<Row  justify="center"   style={{ marginTop: 40 }}>
        <Col xm={24}  sm={16} md={8}>
            { loading  && <Spin/> }
            { error    && <p  style={{ color:"red" }}>{error}</p>}
            { !success && (
                <Form  onFinish={onFinish}  layout="vertical">
                   {/* 이메일 입력 + 중복 검사 */}   
                    <Form.Item
                        label="이메일"
                        name="email"
                        hasFeedback
                        rules={[{ required: true, message: '이메일을 입력하세요.' } , {
                            validator: async (_, value) => { // _ 필드관련정보 , value 사용자가 입력한값
                                //입력값이 없으면 검사 통과
                                if (!value) return Promise.resolve();
                                try {
                                    //서버에 이메일 중복여부확인
                                const res = await axios.get(
                                    `/auth/check-email?email=${encodeURIComponent(value)}`
                                );
                                // 서버응답이 true라면  → 이미 사용중인 이메일
                                if (res?.data === true) {
                                    return Promise.reject(
                                    new Error("이미 사용 중인 이메일입니다.")
                                    );
                                }
                                // 중복이 아니라면 검사 통과
                                return Promise.resolve();
                                } catch (err) {
                                // 이메일 중복검사 오류
                                console.error("이메일 중복 검사 오류:", err);
                                // 검사 실패처리
                                return Promise.reject(new Error("중복 검사 실패"));
                                } 
                            },
                        }]}
                    >
                    <Input />
                    </Form.Item>

                   {/* 비밀번호 입력 */}
                    <Form.Item
                        label="비밀번호"
                        name="password"
                        rules={[{ required: true, message: '비밀번호를 입력하세요.' }]}
                    >
                    <Input.Password />
                    </Form.Item>

                   {/* 닉네임 입력 + 중복 검사 */}
                    <Form.Item
                        label="닉네임"
                        name="nickname"
                        hasFeedback
                        rules={[{ required: true, message: '닉네임을 입력하세요.' } , {
                            validator: async (_, value) => {
                                if (!value) return Promise.resolve();
                                try {
                                const res = await axios.get(
                                    `/auth/check-nickname?nickname=${encodeURIComponent(value)}`
                                );
                                if (res?.data === true) {
                                    return Promise.reject(
                                    new Error("이미 사용 중인 닉네임입니다.")
                                    );
                                }
                                return Promise.resolve();
                                } catch (err) {
                                console.error("닉네임 중복 검사 오류:", err);
                                return Promise.reject(new Error("중복 검사 실패"));
                                }
                            },
                        }]}
                    >
                    <Input/>
                    </Form.Item>

                   {/* 프로필 이미지 업로드 */}
                    <Form.Item name="profileImage" label="프로필 이미지">
                    <Upload
                        beforeUpload={() => false} 
                        fileList={fileList}
                        onChange={( {fileList} )=> setFileList(fileList)}
                        maxCount={1}
                    >
                        <Button icon={<UploadOutlined />}>이미지 선택</Button>
                    </Upload>
                    </Form.Item>

                   <Button type="primary"  htmlType="submit"  loading={loading}>
                        회원가입
                   </Button>
                </Form>
            )}
        </Col>
    </Row>);
}
// {
//   "email": "x@x",
//   "password": "x",
//   "nickname": "x",
//   "provider": "local"
// }
// ufile