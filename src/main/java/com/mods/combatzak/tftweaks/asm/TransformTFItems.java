package com.mods.combatzak.tftweaks.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class TransformTFItems implements IClassTransformer {
	
	/**
	 * Performs surgical edits to TFItems class to skip registering certain items to the OreDictionary as dyes
	 * 
	 * @param targetClass compiled TFItems class
	 */
	private static byte[] patchTFItems(byte[] targetClass) {
		ClassNode clazz = new ClassNode(); //get an object representation of the compiled class
		ClassReader reader = new ClassReader(targetClass); //reads the target class into the class node
		reader.accept(clazz, 0); //read the compiled class
		
		for (MethodNode method : clazz.methods) { //iterate across all methods
			if (!method.name.equals("preInit")) continue; //skip to the preinit method
			
			AbstractInsnNode cursor = method.instructions.getFirst(); //get a cursor and initialize it to the start of the method
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 43) cursor = cursor.getNext(); //skip to line 43
			AbstractInsnNode start = cursor; //mark the cursor at line 43
			
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 44) cursor = cursor.getNext(); //skip to line 44
			cursor = cursor.getPrevious(); //retreat to the label
			while (!cursor.getPrevious().equals(start)) method.instructions.remove(cursor.getPrevious()); //delete everything back to the previous line number
			
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 45) cursor = cursor.getNext(); //skip to line 45
			start = cursor; //mark the cursor at line 45
			
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 46) cursor = cursor.getNext(); //skip to line 46
			cursor = cursor.getPrevious(); //retreat to the label
			while (!cursor.getPrevious().equals(start)) method.instructions.remove(cursor.getPrevious()); //delete everything back to the previous line number
			
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 49) cursor = cursor.getNext(); //skip to line 49
			start = cursor; //mark the cursor at line 49
			
			while (cursor.getType() != AbstractInsnNode.LINE || ((LineNumberNode)cursor).line != 50) cursor = cursor.getNext(); //skip to line 50
			cursor = cursor.getPrevious(); //retreat to the label
			while (!cursor.getPrevious().equals(start)) method.instructions.remove(cursor.getPrevious()); //delete everything back to the previous line number
			
			break;
		}
		
		ClassWriter writer = new ClassWriter(0); //make a writer to write out the modified class
		clazz.accept(writer); //write the modified class

		return writer.toByteArray(); //serialize the class and return it
	}
	
	///IClassTransformer Implementation
	
	/**
	 * Looks for the target class(es) and modifies them
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("cofh.thermalfoundation.item.TFItems")) //if the target class is found...
			return patchTFItems(basicClass); //patch it and return
		
		return basicClass; //otherwise leave it alone
	}

}
