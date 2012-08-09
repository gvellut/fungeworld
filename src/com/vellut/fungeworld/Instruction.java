package com.vellut.fungeworld;

public class Instruction {

	private InstructionType instructionType;
	private Object attachedData;

	public Instruction(InstructionType instructionType) {
		this.instructionType = instructionType;
	}

	public InstructionType getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(InstructionType instructionType) {
		this.instructionType = instructionType;
	}

	public Object getAttachedData() {
		return attachedData;
	}

	public void setAttachedData(Object attachedData) {
		this.attachedData = attachedData;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Instruction) {
			Instruction instr = (Instruction) object;
			if (instr.instructionType == this.instructionType) {
				if(instr.attachedData != null && this.attachedData != null) {
					return (instr.attachedData.equals(this.attachedData));
				}
				return true;
			}
		}
		return false;
	}

}
