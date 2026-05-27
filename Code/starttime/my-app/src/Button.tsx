import React from 'react'

interface ButtonProps {
    link: string;
    text: string;
}
const Button:React.FC<ButtonProps> = ({link, text}) => {
  return (
    <div className='flex justify-center items-center min-h-screen'>
        <a href={link} className="px-6 py-3 border-2 border-blue-500 text-blue-500 bg-white rounded-lg shadow-lg 
         transition duration-300  hover:bg-blue-500 hover:text-white">{text}</a>
    </div>
  );
};


export default Button;
