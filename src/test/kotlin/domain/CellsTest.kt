package domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CellsTest : FunSpec({
    context("Cells.create") {
        test("입력한 행, 열 수만큼의 Cell을 생성한다") {
            // given
            val rows = 3
            val cols = 4

            // when
            val cells = Cells.create(rows, cols)

            // then
            cells.allCells().size shouldBe (rows * cols)
        }
    }

    context("nonMinedCells") {
        test("초기 상태에서 모든 셀은 지뢰가 없으므로 모든 셀이 반환된다") {
            // given
            val cells = Cells.create(2, 2)

            // when
            val nonMined = cells.nonMinedCells()

            // then
            nonMined.size shouldBe 4
            nonMined.all { !it.hasMine } shouldBe true
        }

        test("일부 셀에 지뢰를 추가한 뒤 지뢰가 없는 셀만 반환한다") {
            // given
            val cells = Cells.create(2, 2)
            val targetCell = cells.allCells().random()
            val updatedCells = cells.updateCell(targetCell)

            // when
            val nonMined = updatedCells.nonMinedCells()

            // then
            nonMined.size shouldBe (4 - 1)
            nonMined.none { it.hasMine } shouldBe true
        }
    }

    context("withMineOn") {
        test("지정한 셀에 지뢰를 추가한 새로운 Cells를 반환한다") {
            // given
            val cells = Cells.create(2, 2)
            val targetCell = cells.allCells().last()

            // when
            val updatedCells = cells.updateCell(targetCell)

            // then
            val updatedTargetCell = updatedCells.allCells().first { it.position == targetCell.position }
            updatedTargetCell.hasMine shouldBe true
        }

        test("여러번 지뢰를 설치해도 호출한 순서대로 셀에 지뢰가 추가된다") {
            // given
            val cells = Cells.create(3, 3)
            val targetCells = cells.allCells().take(2) // 처음 두 셀에 차례로 지뢰 설치
            val updatedOnce = cells.updateCell(targetCells[0])

            // when
            val updatedTwice = updatedOnce.updateCell(targetCells[1])

            // then
            updatedTwice.allCells().count { it.hasMine } shouldBe 2
            updatedTwice.allCells().any { it.position == targetCells[0].position && it.hasMine } shouldBe true
            updatedTwice.allCells().any { it.position == targetCells[1].position && it.hasMine } shouldBe true
        }
    }
})
