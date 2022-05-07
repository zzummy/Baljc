import Image from "next/image";
import styled from "styled-components";

import help from "../../public/assets/img/community/help.png";
import together from "../../public/assets/img/community/together.png";
import town from "../../public/assets/img/community/town.png";
import CommunityCard from "./CommunityCard";

const Container = styled.div`
  background-color: #f4f4f4;
  padding: 1rem 0 0 0;
`;
const Header = styled.div`
  padding: 1rem;
  margin: 0 0 0 0;
  background-color: #ffffff;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
`;

const FlexColumn = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const BodyContainer = styled.div`
  /* display: flex; */
  padding: 0 0 1rem 0;
`;

export default function CommunityList() {
  return (
    <Container>
      <Header>
        <FlexColumn>
          <Image src={help} alt="부탁해요" width="70%" height="70%" />
          <div>부탁해요</div>
        </FlexColumn>
        <FlexColumn>
          <Image src={together} alt="같이해요" width="70%" height="70%" />
          <div>같이 해요</div>
        </FlexColumn>
        <FlexColumn>
          <Image src={town} alt="동네정보" width="70%" height="70%" />
          <div>동네 정보</div>
        </FlexColumn>
      </Header>
      <BodyContainer>
        <CommunityCard />
        <CommunityCard />
        <CommunityCard />
      </BodyContainer>
    </Container>
  );
}