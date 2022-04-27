import React, { useState } from "react";
import styled from "styled-components";

import Header from "../../components/common/Header";
import ButtonBottom from "../../components/common/ButtonBottom";
import ButtonToggleSalaryType from "../../components/mypage/survey/ButtonToggleSalaryType";
// import ButtonTrashCan from "../../components/common/ButtonTrashCan";

const PageContainer = styled.main`
  padding: 0 2rem;
`;

// const ButtonContainer = styled.div`
//   display: flex;
//   gap: 1.6rem;
// `;

const InputContainer = styled.div`
  width: 100%;
  border-bottom: 1px solid #cccccc;
  margin: 1.6rem 0 4rem 0;
  color: #cccccc;
  :focus-within {
    border-bottom: 1px solid #3d3d3d;
    span {
      color: #3d3d3d;
    }
  }
  font-size: 2rem;
  display: flex;
  gap: 1rem;
`;

// 입력 Input 뒤에 단위 나타내는 텍스트
const InputUnit = styled.span<{ hasValue: boolean }>`
  line-height: 2rem;
  word-break: keep-all;
  color: ${(props) => (props.hasValue ? "#3d3d3d" : "#cccccc")};
`;

const StyledInput = styled.input`
  width: 100%;
  font-size: 2rem;
  text-align: end;
  border: none;
  outline: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  color: #3d3d3d;
  ::placeholder {
    color: #cccccc;
  }
`;

const StyledLabel = styled.label`
  font-size: 2rem;
  color: #3d3d3d;
  /* font-weight: 500; */
  display: inline-block;
  margin-top: 1.6rem;
`;

const ButtonToggleContainer = styled.div`
  margin-top: 1.6rem;
`;

type TypeSalary = "월급" | "시급" | "없음";

interface SurveyInputForm {
  nickname: string;
  salaryType: TypeSalary;
  salary: number;
  workTime: number;
  monthBudget: number;
}

export default function Survey() {
  const [surveyForm, setSurveyForm] = useState<SurveyInputForm>({
    nickname: "",
    salaryType: "월급",
    salary: 0,
    workTime: 0,
    monthBudget: 0,
  } as SurveyInputForm);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
    const target = event.target;
    const value = target.value;
    const name = target.name;

    setSurveyForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  function handleToggleButton(value: TypeSalary) {
    setSurveyForm((prev) => ({
      ...prev,
      salaryType: value,
    }));
  }

  return (
    <>
      <Header label="설문 조사"></Header>
      <PageContainer>
        <StyledLabel htmlFor="nickname">닉네임</StyledLabel>
        <InputContainer>
          <StyledInput
            name="nickname"
            type="text"
            placeholder="닉네임을 입력해주세요"
            value={surveyForm.nickname}
            onChange={handleInputChange}
          />
        </InputContainer>

        <StyledLabel htmlFor="salary">급여</StyledLabel>
        <ButtonToggleContainer>
          <ButtonToggleSalaryType
            selectedSalaryType={surveyForm.salaryType}
            handleToggleButton={handleToggleButton}
          />
        </ButtonToggleContainer>
        {surveyForm?.salaryType !== "없음" && (
          <>
            <InputContainer>
              <StyledInput
                name="salary"
                type="number"
                placeholder="0"
                value={surveyForm.salary || ""}
                onChange={handleInputChange}
              />
              <InputUnit hasValue={surveyForm.salary > 0}>원</InputUnit>
            </InputContainer>

            <StyledLabel htmlFor="salary">
              한 주에 몇 시간 일하시나요?
            </StyledLabel>
            <InputContainer>
              <StyledInput
                name="workTime"
                type="number"
                placeholder="0"
                value={surveyForm.workTime || ""}
                onChange={handleInputChange}
              />
              <InputUnit hasValue={surveyForm.workTime > 0}>시간</InputUnit>
            </InputContainer>
          </>
        )}

        <StyledLabel htmlFor="monthBudget">한 달 예산</StyledLabel>
        <InputContainer>
          <StyledInput
            name="monthBudget"
            type="number"
            placeholder="0"
            value={surveyForm.monthBudget || 0}
            onChange={handleInputChange}
          />
          <InputUnit hasValue={surveyForm.monthBudget > 0}>원</InputUnit>
        </InputContainer>

        <ButtonBottom label="가입" />
        {/* <ButtonContainer>
          <ButtonTrashCan />
          <ButtonBottom label="수정" />
        </ButtonContainer> */}
      </PageContainer>
    </>
  );
}