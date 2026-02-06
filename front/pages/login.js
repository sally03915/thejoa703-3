// pages/login.js
import React from "react";
import { useDispatch, useSelector } from "react-redux"; // 리듀서 상태관리
import { Row, Col, Form, Input, Button, Spin, message } from "antd";  // 컴포넌트
import { useRouter } from "next/router"; // 경로
import { loginRequest} from "../reducers/authReducer"; //액션 - 로그인성공, 로그아웃

export default function LoginPage(){
    // 코드
    const dispatch = useDispatch();
    const router   = useRouter();
    const {user,loading,error} = useSelector( (state) => state.auth );

    //로그인 버튼
    // const onFinish = async(values)=>{ 
    //     try{    // api.post → saga 
    //         const res = await  api.post("/auth/login"
    //                                     , {...values , provider:"local"} 
    //                                     , { headers:{"Content-Type":"application/json"} });
    //         const {accessToken , user} = res.data;           
            
    //         if(user && accessToken){
    //             localStorage.setItem("accessToken" , accessToken);
    //             dispatch(loginSuccess({user,accessToken}));
    //             message.success(`${user.nickname}님 환영합니다!`);
    //             router.push("/mypage");
    //         }else{
    //             dispatch(logout());
    //             message.error("로그인 정보를 확인할 수 없습니다.");
    //         }
    //     }catch(err){
    //             dispatch(logout());
    //             message.error("로그인 실패: 이메일/비밀번호를 확인하세요.");
    //     } 
    // };

    const onFinish = async(values)=>{ 
        dispatch( loginRequest({...values , provider:"local"}))
    }
 
    //소셜 로그인 핸들러추가
    const handleSocialLogin=(provider)=>{ 
        window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/oauth2/authorization/${provider}`;
    }; 
    // 화면
    return (<Row justify="center"  style={{marginTop: 40}} >
        <Col  xs={24}  sm={16}  md={8}>
            { loading  && <Spin/> }
            { error    && <p  style={{ color:"red" }}>{error}</p>}
            { !user  || !user.nickname ? (
                <>
                    {/*   local 로그인 폼 */}
                    <Form onFinish={onFinish}> 
                        <Form.Item 
                        name="email"
                        rules={[{ required: true, message: "이메일을 입력하세요." }]}
                        >
                            <Input placeholder="이메일" />
                        </Form.Item>

                        <Form.Item 
                            name="password"
                            rules={[{ required: true, message: "비밀번호를 입력하세요." }]}
                        >
                        <Input.Password placeholder="비밀번호" />
                        </Form.Item>         
                        <div style={{textAlign : 'center'}}>
                            <Button type="primary"  htmlType="submit"  
                                    loading={loading}  style={{width:'200px' , height:'50px'}}>
                                로그인
                            </Button>
                        </div>
                    </Form>
                    {/*   소셜  로그인 이미지 버튼 */}
                    <div style={{ marginTop: 20, textAlign: "center" }}>
                        <img
                            src="/images/google.png"       alt="Google Login"
                            style={{ cursor: "pointer", width: "200px", marginBottom: "10px" }}
                            onClick={()=> handleSocialLogin("google")}
                        />
                    </div> 
                    <div style={{ marginTop: 20, textAlign: "center" }}>
                        <img
                            src="/images/kakao.png"      alt="Kakao Login"
                            style={{ cursor: "pointer", width: "200px", marginBottom: "10px" }}
                            onClick={()=> handleSocialLogin("kakao")}
                        />
                    </div>
                    <div style={{ marginTop: 20, textAlign: "center" }}>
                        <img
                            src="/images/naver.png"      alt="Naver Login"
                            style={{ cursor: "pointer", width: "200px", marginBottom: "10px" }}
                            onClick={()=> handleSocialLogin("naver")}
                        />
                    </div>
                </>
            ) : ( <p> {user.nickname} 님 환영합니다!</p>)}
        </Col>
    </Row>
    );
}

// SSR 단순렌더 : 서버에서 데이터를 가져오거나 가공하지 않고, 그냥 페이지 컴포넌트를 서버에서 그려서 내겨주는것.
export async function getServerSideProps() {
  return { props: {} };
}
